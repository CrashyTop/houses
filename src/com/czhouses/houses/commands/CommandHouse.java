package com.czhouses.houses.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.cubeshop.cash.Saldo;
import com.czhouses.HousesCore;
import com.czhouses.api.Cuboid;
import com.czhouses.api.Item;
import com.czhouses.api.secToMin;
import com.czhouses.config.ConfigManager;
import com.czhouses.houses.House;
import com.czhouses.houses.HouseBlock;
import com.czhouses.houses.HouseManager;
import com.czhouses.houses.SellType;
import com.czhouses.houses.recovery.Recovery;
import com.czhouses.houses.recovery.RecoveryManager;

import net.milkbowl.vault.economy.Economy;

public class CommandHouse extends Command {

	private HouseManager houseManager;

	private Map<String, Location> pos1;
	private Map<String, Location> pos2;
	private Map<String, Location> join;
	private Map<String, Location> sign;

	public CommandHouse(HouseManager houseManager) {
		super("house", "", "", Arrays.asList("houses", "casa", "casas", "apartamento", "ap"));
		this.houseManager = houseManager;

		this.pos1 = new HashMap<String, Location>();
		this.pos2 = new HashMap<String, Location>();
		this.join = new HashMap<String, Location>();
		this.sign = new HashMap<String, Location>();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String commandName, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length >= 1) {
			if (player.hasPermission("house.admin")) {
				if (args[0].equalsIgnoreCase("selecionar")) {
					if (args.length == 1) {
						player.sendMessage("§7/" + commandName + " §aselecionar <pos1 | pos2>");
						return false;
					}

					Block block = player.getTargetBlock((HashSet<Byte>) null, 5);

					if (block == null) {
						player.sendMessage("§cVocê precisa estar olhando para um bloco!");
						return false;
					}

					if (args[1].equalsIgnoreCase("pos1")) {
						pos1.put(player.getName(), block.getLocation());
						player.sendMessage("§aLocalização 'pos1' setada!");
						return false;
					}

					if (args[1].equalsIgnoreCase("pos2")) {
						pos2.put(player.getName(), block.getLocation());
						player.sendMessage("§aLocalização 'pos2' setada!");
						return false;
					}

					player.sendMessage("§7/" + commandName + " §aselecionar <pos1 | pos2>");
					return false;
				}
				if (args[0].equalsIgnoreCase("entrada")) {
					join.put(player.getName(), player.getLocation());

					player.sendMessage("§aVocê setou a entrada!");
					return false;
				}

				if (args[0].equalsIgnoreCase("placa")) {
					Block block = player.getTargetBlock((HashSet<Byte>) null, 5);

					if (block == null) {
						player.sendMessage("§cVocê precisa estar olhando para um bloco!");
						return false;
					}

					if (!block.getType().toString().toLowerCase().contains("sign")) {
						player.sendMessage("§cVocê precisa estar olhando para uma placa!");
						return false;
					}

					Location location = block.getLocation();
					sign.put(player.getName(), location);
					player.sendMessage("§aVocê setou a localização da placa!");
					return false;

				}
				if (args[0].equalsIgnoreCase("criar")) {
					if (args.length <= 5) {
						player.sendMessage("§7/" + commandName
								+ " §acriar <preço de venda por dia> <cash|money> <andar> <regionName> <nome>");
						return false;
					}

					double price = 0;
					int andar = 0;

					try {
						price = Double.parseDouble(args[1]);
						andar = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						player.sendMessage("§cUse apenas números!");
						return false;
					}
					SellType sellType = null;

					try {
						sellType = SellType.valueOf(args[2].toUpperCase());
					} catch (Exception e) {
					}

					if (andar <= 0) {
						player.sendMessage("§aO andar é necessário ser maior que zero!");
						return false;
					}

					if (sellType == null) {
						player.sendMessage("§cNão foi encontrado o tipo " + args[2]);
						return false;
					}

					String regionName = args[4];

					if (!houseManager.getRegionByName(regionName)) {
						player.sendMessage("§cRegião não encontrada!");
						return false;
					}

					StringBuilder sb = new StringBuilder();
					for (int x = 5; x < args.length; x++) {
						sb.append(args[x] + " ");
					}

					Location loc1 = pos1.get(player.getName());
					Location loc2 = pos2.get(player.getName());

					if (loc1 == null || loc2 == null) {
						player.sendMessage("§cUma das localizações não está setada!");
						return false;
					}

					Location entry = join.get(player.getName());

					if (entry == null) {
						player.sendMessage("§cVocê não setou a entrada!");
						return false;
					}

					Location signWall = sign.get(player.getName());

					if (signWall == null) {
						player.sendMessage("§cVocê não setou a placa!");
						return false;
					}

					Cuboid cuboid = new Cuboid(loc1, loc2);

					List<Block> blocks = cuboid.getBlocks();
					Set<HouseBlock> houseBlocks = new HashSet<HouseBlock>();
					blocks.stream().forEach(i -> houseBlocks.add(new HouseBlock(i.getLocation(), i)));

					houseManager.add(cuboid, sb.toString(), price, sellType, entry, andar, houseBlocks, signWall,
							regionName);
					player.sendMessage("§aCasa criada com sucesso!");
					return false;
				}

				if (args[0].equalsIgnoreCase("deletar")) {
					if (args.length == 1) {
						player.sendMessage("§7/" + commandName + " §adeletar <id>");
						return false;
					}

					int id = 0;

					try {
						id = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("§cUse apenas números!");
						return false;
					}

					House house = houseManager.getHouseByID(id);

					if (house == null) {
						player.sendMessage("§cNão foi encontrado uma casa com o ID " + id + "!");
						return false;
					}

					house.deleteOwner();

					Block block = house.getSign().getBlock();

					if (block != null) {
						if (block.getState() instanceof Sign) {
							Sign sign = (Sign) block.getState();

							sign.setLine(0, "");
							sign.setLine(1, "");
							sign.setLine(2, "");
							sign.setLine(3, "");
							sign.update();
						}
					}

					houseManager.remove(house);
					player.sendMessage("§aCasa deletada!");
					return false;
				}

				if (args[0].equalsIgnoreCase("holograma")) {
					houseManager.setLocation(player.getLocation());
					player.sendMessage("§aLocalização do holograma atualizada!");
					return false;
				}

			}
			if (args[0].equalsIgnoreCase("alugar")) {
				if (args.length <= 2) {
					player.sendMessage("§aUse o comando §7/" + commandName + " §aalugar <id> <tempo em dias>");
					return false;
				}

				if (houseManager.getHouseByOwner(player.getName()) != null) {
					player.sendMessage("§cVocê já possui uma casa alugada!");
					return false;
				}

				int id = 0;
				int days = 0;

				try {
					id = Integer.parseInt(args[1]);
					days = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					player.sendMessage("§cUse apenas números!");
					return false;
				}

				if (days <= 0) {
					player.sendMessage("§aA quantia mínima de dias é 1!");
					return false;
				}
				if (days >= 31) {
					player.sendMessage("§aA quantia máxima de dias é 30!");
					return false;
				}

				House house = houseManager.getHouseByID(id);

				if (house == null) {
					player.sendMessage("§cNão foi encontrado uma casa com o ID " + id + "!");
					return false;
				}

				if (house.getOwner().equalsIgnoreCase(player.getName())) {
					if (house.getDays() > 30 || house.getDays() + days > 30) {
						player.sendMessage("§cVocê não pode adicionar mais dias nesta casa!");
						return false;
					}

					// system to remove
					double priceTotal = house.getPriceSell() * days;

					if (house.getSellType() == SellType.CASH) {
						Saldo cashSystem = Saldo.getInstance();
						double cash = cashSystem.getSaldo(player.getName());

						if (cash < priceTotal) {
							player.sendMessage("§cVocê não possui '" + priceTotal + "' cash's para comprar isso!");
							return false;
						}

						cashSystem.setSaldo(player.getName(), priceTotal);
						player.sendMessage("§cRemovidos " + priceTotal + " cash's!");
					} else {
						Economy economy = HousesCore.getInstance().getEconomy();
						double balance = economy.getBalance(player.getName());

						if (balance < priceTotal) {
							player.sendMessage("§cVocê não possui '" + priceTotal + "' coins para comprar isso!");
							return false;
						}

						economy.withdrawPlayer(player.getName(), priceTotal);
						player.sendMessage("§cRemovidos " + priceTotal + " coins!");
					}
					// system to remove

					house.setDays(house.getDays() + days);

					houseManager.update(house);

					ConfigManager configManager = HousesCore.getInstance().getConfigManager();
					if (house.getSellType() == SellType.CASH) {
						for (String command : configManager.getCashEnable()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									command.replace("%player%", house.getOwner()));
						}
					} else {
						for (String command : configManager.getMoneyEnable()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									command.replace("%player%", house.getOwner()));
						}
					}

					player.sendMessage("§aVocê adicionou " + days + " dias na casa '" + house.getName() + "', total de "
							+ house.getDays() + " dias!");

					// add player on region
					houseManager.addMemberInRegion(player,
							house.getHouseBlocks().stream().findFirst().get().getLocation(), house.getRegionName());

					return false;
				} else if (!house.hasOwner()) {
					if (days > 30) {
						player.sendMessage("§cVocê não pode adicionar mais dias nesta casa!");
						return false;
					}
					// system to remove
					double priceTotal = house.getPriceSell() * days;

					if (house.getSellType() == SellType.CASH) {
						Saldo cashSystem = Saldo.getInstance();
						double cash = cashSystem.getSaldo(player.getName());

						if (cash < priceTotal) {
							player.sendMessage("§cVocê não possui '" + priceTotal + "' cash's para comprar isso!");
							return false;
						}

						cashSystem.setSaldo(player.getName(), priceTotal);
						player.sendMessage("§cRemovidos " + priceTotal + " cash's!");
					} else {
						Economy economy = HousesCore.getInstance().getEconomy();
						double balance = economy.getBalance(player.getName());

						if (balance < priceTotal) {
							player.sendMessage("§cVocê não possui '" + priceTotal + "' coins para comprar isso!");
							return false;
						}

						economy.withdrawPlayer(player.getName(), priceTotal);
						player.sendMessage("§cRemovidos " + priceTotal + " coins!");
					}
					// system to remove

					house.setDateBuy(System.currentTimeMillis());
					house.setDays(days);
					house.setOwner(player.getName());

					houseManager.update(house);

					ConfigManager configManager = HousesCore.getInstance().getConfigManager();
					if (house.getSellType() == SellType.CASH) {
						for (String command : configManager.getCashEnable()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									command.replace("%player%", house.getOwner()));
						}
					} else {
						for (String command : configManager.getMoneyEnable()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									command.replace("%player%", house.getOwner()));
						}
					}

					player.sendMessage("§aVocê alugou a casa '" + house.getName() + "', com o total de "
							+ house.getDays() + " dias!");

					// add player on region
					houseManager.addMemberInRegion(player,
							house.getHouseBlocks().stream().findFirst().get().getLocation(), house.getRegionName());

					return false;
				}

				player.sendMessage("§cEsta casa não pertence a você!");
				return false;
			}

			if (args[0].equalsIgnoreCase("tempo")) {
				House house = null;

				if (args.length == 1) {
					house = houseManager.getHouseByOwner(player);
				} else {
					try {
						house = houseManager.getHouseByID(Integer.parseInt(args[1]));
					} catch (NumberFormatException e) {
						house = houseManager.getHouseByOwner(args[1]);
					}
				}

				if (house == null) {
					player.sendMessage("§cNão foi encontrado esta casa!");
					return false;
				}

				player.sendMessage("");
				player.sendMessage("§7 ID: §a" + house.getId());
				player.sendMessage("§7 Nome: §a" + house.getName());
				player.sendMessage("§7 Andar: §a" + house.getAndar() + "º");
				player.sendMessage("§7 Diária: §a" + house.getPriceSell() + " §7Tipo: §a"
						+ WordUtils.capitalize(house.getSellType().getName()));
				if (house.hasOwner()) {
					player.sendMessage("§7 Dono: §a" + house.getOwner());
					player.sendMessage("§7 Tempo alugado: §a" + house.getDays() + " dias");
					player.sendMessage("§7 Expira em: §a"
							+ new secToMin((int) (house.getDateExpire() - System.currentTimeMillis()) / 1000).toTime());
				} else {
					player.sendMessage("§a Não há um dono!");
				}
				player.sendMessage("");
				return false;
			}

			if (args[0].equalsIgnoreCase("ver")) {
				if (args.length == 1) {
					player.sendMessage("§7/" + commandName + " §aver <id | jogador>");
					return false;
				}
				House house = null;

				try {
					house = houseManager.getHouseByID(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) {
					house = houseManager.getHouseByOwner(args[1]);
				}

				if (house == null) {
					player.sendMessage("§cNão foi encontrado esta casa!");
					return false;
				}

				if (house.getEntry() == null) {
					player.sendMessage("§cLocalização de entrada inválida!");
					return false;
				}

				player.teleport(house.getEntry());
				player.sendMessage("§aTeleportado para a entrada de '" + house.getName() + "'!");
				return false;
			}

			if (args[0].equalsIgnoreCase("listar")) {
				if (args.length == 1) {
					player.sendMessage("§7/" + commandName + " §alistar <nome>");
					return false;
				}

				Set<House> houses = houseManager.getHouses().stream()
						.filter(i -> i.getName().toLowerCase().contains(args[1].toLowerCase()))
						.collect(Collectors.toSet());

				Inventory inv = Bukkit.createInventory(null, 6 * 9, "§7Apartamento");

				inv.setItem(0, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a1º Andar"));
				inv.setItem(9, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a2º Andar"));
				inv.setItem(18, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a3º Andar"));
				inv.setItem(27, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a4º Andar"));
				inv.setItem(36, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a5º Andar"));
				inv.setItem(45, new Item(Material.SKULL_ITEM).setDurabilityy((short) 3).setOwner("MHF_ArrowRight")
						.setDisplayName("§a6º Andar"));

				int a1 = 1;
				int a2 = 10;
				int a3 = 19;
				int a4 = 28;
				int a5 = 37;
				int a6 = 46;

				for (House house : houses) {
					int slot = 0;
					if (house.getAndar() == 1) {
						slot = a1;
						a1 = a1 + 1 >= 10 ? 9 : a1 + 1;
					} else if (house.getAndar() == 2) {
						slot = a2;
						a2 = a2 + 1 >= 19 ? 18 : a2 + 1;
					} else if (house.getAndar() == 3) {
						slot = a3;
						a3 = a3 + 1 >= 28 ? 27 : a3 + 1;
					} else if (house.getAndar() == 4) {
						slot = a4;
						a4 = a4 + 1 >= 37 ? 36 : a4 + 1;
					} else if (house.getAndar() == 5) {
						slot = a5;
						a5 = a5 + 1 >= 46 ? 45 : a5 + 1;
					} else if (house.getAndar() == 6) {
						slot = a6;
						a6 = a6 + 1 >= 54 ? 53 : a6 + 1;
					}

					short sh = 5;

					List<String> lore = new ArrayList<String>();
					lore.add("§7 ID: §a" + house.getId());
					lore.add("§7 Andar: §a" + house.getAndar() + "º");
					lore.add("§7 Diária: §a" + house.getPriceSell() + " §7Tipo: §a"
							+ WordUtils.capitalize(house.getSellType().getName()));
					if (house.hasOwner()) {
						lore.add("§7 Dono: §a" + house.getOwner());
						lore.add("§7 Tempo alugado: §a" + house.getDays() + " dias");
						lore.add("§7 Expira em: §a"
								+ new secToMin((int) (house.getDateExpire() - System.currentTimeMillis()) / 1000)
										.toTime());
						sh = 14;
					} else {
						lore.add("§a Não há um dono!");
					}
					inv.setItem(slot, new Item(Material.WOOL).setDurabilityy(sh).setDisplayName("§a" + house.getName())
							.setLore(lore));
				}

				player.openInventory(inv);
				return false;
			}

			if (args[0].equalsIgnoreCase("itens") || args[0].equalsIgnoreCase("item")) {
				RecoveryManager recoveryManager = houseManager.getRecoveryManager();
				Recovery recovery = recoveryManager.getRecovery(player.getName());

				if (recovery == null) {
					player.sendMessage("§cVocê não possui itens para recuperar!");
					return false;
				}

				if (recovery.isExpired()) {
					player.sendMessage("§cSeu tempo para recuperar itens expirou!");
					recoveryManager.remove(recovery);
					return false;
				}

				recoveryManager.openMenu(player, recovery);
				return false;
			}
		}

		player.sendMessage("§aComandos disponíveis:");
		player.sendMessage("§7/" + commandName + " §aalugar <id> <tempo>");
		player.sendMessage("§7/" + commandName + " §atempo <jogador>");
		player.sendMessage("§7/" + commandName + " §aver <id | jogador>");
		player.sendMessage("§7/" + commandName + " §alistar <nome>");
		player.sendMessage("§7/" + commandName + " §aitens");

		if (player.hasPermission("house.admin")) {
			player.sendMessage("§7/" + commandName + " §acriar <preço de venda por dia> <cash|money> <andar> <nome>");
			player.sendMessage("§7/" + commandName + " §adeletar <id>");
			player.sendMessage("§7/" + commandName + " §aselecionar <pos1 | pos2>");
			player.sendMessage("§7/" + commandName + " §aentrada");
			player.sendMessage("§7/" + commandName + " §aplaca");
			player.sendMessage("§7/" + commandName + " §aholograma");
		}
		return false;
	}

}
