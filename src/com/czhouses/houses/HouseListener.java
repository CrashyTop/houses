package com.czhouses.houses;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.czhouses.HousesCore;
import com.czhouses.api.LocationUtils;
import com.czhouses.api.secToMin;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class HouseListener implements Listener {

	private HouseManager houseManager;
	private BukkitTask task;

	public HouseListener(HouseManager houseManager) {
		this.houseManager = houseManager;

		schedule();
	}

	public boolean canBuild(Player player, Block block) {
		WorldGuardPlugin plugin = WGBukkit.getPlugin();
		return plugin.canBuild(player, block.getRelative(0, -1, 0));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		House house = houseManager.getHouseByOwner(player);

		if (house != null) {
			long timeBuy = house.getDateBuy();
			long days = TimeUnit.DAYS.toMillis(house.getDays());

			double expireIn = timeBuy + days;
			double leftTime = (expireIn - System.currentTimeMillis()) / 1000;

			if (leftTime <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis(5)) {
				TextComponent m1 = new TextComponent("§c[CZHOME] Esta acabando seu tempo de aluguel!");
				m1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7Clique para renovar!").create()));
				m1.setClickEvent(
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ap alugar " + house.getId() + " <tempo>"));

				player.spigot().sendMessage(m1);
			} else if (leftTime <= System.currentTimeMillis()) {
				TextComponent m1 = new TextComponent(
						"§c[CZHOME] Seu tempo de aluguel acabou. Para recuperar seus itens digite /ap itens");
				m1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7Clique coletar os itens").create()));
				m1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ap itens"));

				player.spigot().sendMessage(m1);
			}
		}
	}

	public void schedule() {
		if (task != null) {
			task.cancel();
		}

		task = new BukkitRunnable() {
			@Override
			public void run() {
				houseManager.getAnimation().onSwitch();

				Set<House> houses = new HashSet<House>();
				Set<House> houses2 = new HashSet<House>(houseManager.getHouses());
				houses2.stream().forEach(i -> {

					if (i.getDateExpire() > 0) {
						if (System.currentTimeMillis() >= i.getDateExpire()) {
							houses.add(i);
						}
					}

					Block block = i.getSign().getBlock();

					if (block != null) {
						Sign sign = (Sign) block.getState();

						if (i.hasOwner()) {

							long timeBuy = i.getDateBuy();
							long days = TimeUnit.DAYS.toMillis(i.getDays());

							double expireIn = timeBuy + days;
							double leftTime = (expireIn - System.currentTimeMillis()) / 1000;

							sign.setLine(0, i.getName());
							sign.setLine(1, "§c§lALUGADO POR");
							sign.setLine(2, "§c" + i.getOwner());
							sign.setLine(3, "§d" + new secToMin((int) leftTime).toTime());
							sign.update();
						} else {
							sign.setLine(0, i.getName());
							sign.setLine(1, "§c§lALUGUEL:");
							sign.setLine(2, "§a" + i.getPriceSell() + " " + i.getSellType().getName());
							sign.setLine(3, "§aClique para ver!");
							sign.update();
						}
					}
				});

				if (houses.size() > 0) {
					houses.stream().forEach(i -> {
						i.deleteOwner();
					});
				}
			}
		}.runTaskTimer(HousesCore.getInstance(), 20, 20);
	}

//	@EventHandler
//	public void onMove(PlayerMoveEvent event) {
//		Player player = event.getPlayer();
//
//		House house = houseManager.getHouseByLocation(player.getLocation());
//
//		if (house == null) {
//			return;
//		}
//
//		if (house.hasOwner()) {
//			if (!house.getOwner().equalsIgnoreCase(player.getName()) && !player.hasPermission("house.admin")) {
//				player.teleport(house.getEntry());
//				player.sendMessage("§cVocê não pode entrar em uma casa que não pertence a você!");
//			}
//		}
//	}

	@EventHandler
	public void onInv(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (player.getOpenInventory() == null) {
			return;
		}

		if (!player.getOpenInventory().getTitle().equalsIgnoreCase("§7Apartamento")) {
			return;
		}

//		List<String> lore = new ArrayList<String>();
//		lore.add("§7 ID: §a" + house.getId());
//		lore.add("§7 Andar: §a" + house.getAndar() + "º");
//		lore.add("§7 Diária: §a" + house.getPriceSell() + " §7Tipo: §a"
//				+ WordUtils.capitalize(house.getSellType().getName()));
//		if (house.hasOwner()) {
//			lore.add("§7 Dono: §a" + house.getOwner());
//			lore.add("§7 Tempo alugado: §a" + house.getDays() + " dias");
//			lore.add("§7 Expira em: §a"
//					+ new secToMin((int) (house.getDateExpire() - System.currentTimeMillis()) / 1000)
//							.toTime());
//			sh = 14;
//		} else {
//			lore.add("§a Não há um dono!");
//		}
//		inv.setItem(slot, new Item(Material.WOOL).setDurabilityy(sh).setDisplayName("§a" + house.getName())
//				.setLore(lore));
//	}
		event.setCancelled(true);

		ItemStack currentItem = event.getCurrentItem();

		if (currentItem == null || currentItem.getType() == Material.AIR) {
			return;
		}

		if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasLore()) {
			int id = -1;

			try {
				id = Integer.parseInt(
						ChatColor.stripColor(currentItem.getItemMeta().getLore().get(0)).replaceAll(" ID: ", ""));
			} catch (NumberFormatException e) {
				player.sendMessage("§cErro ao converter números!");
				return;
			}

			player.performCommand("ap ver " + id);
			player.closeInventory();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!event.getAction().toString().toLowerCase().contains("_block")) {
			return;
		}

		Block block = event.getClickedBlock();

		if (block == null) {
			return;
		}

		if (block.getType().toString().toLowerCase().contains("sign")) {
			House h = houseManager.getHouses().stream()
					.filter(i -> LocationUtils.isEquals(i.getSign(), block.getLocation())).findAny().orElse(null);

			if (h != null) {
				if (h.hasOwner()) {
					player.sendMessage("§7Este apartamento já possui um dono!");
					return;
				}
				player.sendMessage(
						"§7A diária deste apartamento é §a" + h.getPriceSell() + " " + h.getSellType().getName());
				TextComponent tx = new TextComponent("§aClique aqui para escolher o tempo de aluguel!");
				tx.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§7Clique para continuar!").create()));
				tx.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
						"/ap alugar " + h.getId() + " <tempo em dias>"));
				player.spigot().sendMessage(tx);
				return;
			}
		}

		if (!canBuild(player, block)) {
			return;
		}

		House house = houseManager.getHouseByLocation(block.getLocation());

		if (house == null) {
			return;
		}

		if ((house.getOwner() == null || !house.getOwner().equalsIgnoreCase(player.getName()))
				&& !player.hasPermission("house.admin")) {
			event.setCancelled(true);
			player.sendMessage("§cVocê não pode fazer isso aqui!");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		Block block = event.getBlock();

		if (block == null) {
			return;
		}

		if (!canBuild(player, block)) {
			return;
		}

		House house = houseManager.getHouseByLocation(block.getLocation());

		if (house == null) {
			return;
		}

		if ((house.getOwner() == null || !house.getOwner().equalsIgnoreCase(player.getName()))
				&& !player.hasPermission("house.admin")) {
			event.setCancelled(true);
			player.sendMessage("§cVocê não pode fazer isso aqui!");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		Block block = event.getBlock();

		if (block == null) {
			return;
		}

		if (!canBuild(player, block)) {
			return;
		}

		House house = houseManager.getHouseByLocation(block.getLocation());

		if (house == null) {
			return;
		}

		if ((house.getOwner() == null || !house.getOwner().equalsIgnoreCase(player.getName()))
				&& !player.hasPermission("house.admin")) {
			event.setCancelled(true);
			player.sendMessage("§cVocê não pode fazer isso aqui!");
		}
	}
}
