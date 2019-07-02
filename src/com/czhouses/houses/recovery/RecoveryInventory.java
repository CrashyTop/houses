package com.czhouses.houses.recovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.czhouses.api.InventoryUtils;

public class RecoveryInventory implements Listener {

	public ArrayList<Inventory> pages = new ArrayList<Inventory>();
	public UUID id;
	public int currpage = 0;
	public static HashMap<UUID, RecoveryInventory> users = new HashMap<UUID, RecoveryInventory>();
	public static final String nextPageName = "§aPróxima página";
	public static final String previousPageName = "§cPágina anterior";
	String name;
	private ItemStack main;
	private ItemStack nextpage;

	public String getName() {
		return name;
	}

	public RecoveryInventory(List<ItemStack> items, String name, Player player) {
		this.id = UUID.randomUUID();

		Inventory page = getBlankPage(name);
		Integer slot = 10;
		for (int i = 0; i < items.size(); i++) {
			if (slot == 44 /* 36 */) {
				this.pages.add(page);
				page = getBlankPage(name);
				slot = 10;
				page.setItem(slot, items.get(i));
			} else {
				page.setItem(slot, items.get(i));
				if (slot == 16 || slot == 25 || slot == 34) {
					slot += 3;
				} else {
					slot += 1;
				}
			}
		}
		this.name = name;
		this.pages.add(page);

		player.openInventory(this.pages.get(this.currpage));
		users.put(player.getUniqueId(), this);
	}

	private Inventory getBlankPage(String name) {
		Inventory page = Bukkit.createInventory(null, 54, name + " §8- Página " + this.currpage);

		this.nextpage = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = nextpage.getItemMeta();
		meta.setDisplayName(nextPageName);
		nextpage.setItemMeta(meta);

		this.main = new ItemStack(Material.ARROW, 1);
		meta = main.getItemMeta();
		meta.setDisplayName(previousPageName);
		main.setItemMeta(meta);

		// page.setItem(45, main);
		page.setItem(53, nextpage);
		return page;
	}

	public RecoveryInventory(InventoryClickEvent event, RecoveryManager recoveryManager) {

		Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getName().startsWith("Recuperar")) {

			this.nextpage = new ItemStack(Material.ARROW, 1);
			ItemMeta meta = nextpage.getItemMeta();
			meta.setDisplayName(nextPageName);
			nextpage.setItemMeta(meta);

			this.main = new ItemStack(Material.ARROW, 1);
			meta = main.getItemMeta();
			meta.setDisplayName(previousPageName);
			main.setItemMeta(meta);

			if (event.getClick() == ClickType.NUMBER_KEY) {
				event.setCancelled(true);
			}

			if (event.getCurrentItem() != null) {
				event.setCancelled(true);

				RecoveryInventory inv = RecoveryInventory.users.get(player.getUniqueId());

				if (event.getCurrentItem().hasItemMeta()) {

					String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

					if (displayName != null) {

						if (displayName.equalsIgnoreCase(previousPageName)) {
							int pag = inv.currpage;
							if (pag - 1 < 0) {
							} else {
								inv.currpage -= 1;
								if (inv.pages.get(inv.currpage) != null) {

									Inventory invs = Bukkit.createInventory(null, 6 * 9,
											inv.getName() + " §8- Página " + inv.currpage + "");

									invs.setContents(inv.pages.get(inv.currpage).getContents());

									if (inv.currpage - 1 < 0) {
										invs.setItem(45, new ItemStack(Material.AIR));
									} else {
										invs.setItem(45, main);
									}
									if (inv.currpage >= inv.pages.size() - 1) {
										invs.setItem(53, new ItemStack(Material.AIR));
									} else {
										invs.setItem(53, nextpage);
									}

									player.openInventory(invs);
									event.setCancelled(true);
									return;
								}
							}
						} else if (displayName.equalsIgnoreCase(nextPageName)) {
							if (inv.currpage >= inv.pages.size() - 1) {
							} else {
								inv.currpage += 1;

								Inventory invs = Bukkit.createInventory(null, 6 * 9,
										inv.getName() + " §8- Página " + inv.currpage + "");

								invs.setContents(inv.pages.get(inv.currpage).getContents());

								if (inv.currpage - 1 < 0) {
									invs.setItem(45, new ItemStack(Material.AIR));
								} else {
									invs.setItem(45, main);
								}

								if (inv.currpage >= inv.pages.size() - 1) {
									invs.setItem(53, new ItemStack(Material.AIR));
								} else {
									invs.setItem(53, nextpage);
								}

								player.openInventory(invs);
								event.setCancelled(true);
								return;
							}
						}
					}

					// interact
					event.setCancelled(true);
					Recovery recovery = recoveryManager.getRecovery(player.getName());

					if (recovery == null) {
						player.closeInventory();
						return;
					}

					boolean removed = false;

					Map<Integer, String> itemStack = new HashMap<Integer, String>(recovery.getItems());
					for (Entry<Integer, String> i : itemStack.entrySet()) {
						ItemStack currentItem = event.getCurrentItem().clone();
						ItemMeta itemMeta = currentItem.getItemMeta();
						List<String> lore = new ArrayList<String>();
						if (itemMeta.hasLore()) {
							for (String lrs : itemMeta.getLore()) {
								if (!lrs.equalsIgnoreCase("§aClique para recuperar este item!")) {
									lore.add(lrs);
								}
							}
						}
						itemMeta.setLore(lore);
						currentItem.setItemMeta(itemMeta);

						if (InventoryUtils.stringToItemStack(i.getValue()).isSimilar(currentItem)) {
							recovery.getItems().remove(i.getKey());
							removed = true;
							break;
						}
					}
					if (removed == false) {
						player.sendMessage("§cOcorreu um erro!");
						return;
					}

					ItemStack itemGive = event.getCurrentItem().clone();

					ItemMeta itemMeta = itemGive.getItemMeta();
					List<String> lore = new ArrayList<String>();
					if (itemMeta.hasLore()) {
						for (String l : itemMeta.getLore()) {
							if (!l.equalsIgnoreCase("§aClique para recuperar este item!")) {
								lore.add(l);
							}
						}
					}
					itemMeta.setLore(lore);
					itemGive.setItemMeta(itemMeta);

					if (player.getInventory().firstEmpty() < 0) {
						player.getWorld().dropItem(player.getLocation(), itemGive);
					} else {
						player.getInventory().addItem(itemGive);
					}

					if (recovery.getItems().size() <= 0) {
						recoveryManager.remove(recovery);
						player.closeInventory();
					} else {
						recoveryManager.update(recovery);
						recoveryManager.openMenu(player, recovery);
					}
				}
			}
		}
	}
}
