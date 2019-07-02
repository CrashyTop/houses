package com.czhouses.houses.recovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.czhouses.HousesCore;
import com.czhouses.houses.recovery.storage.RecoverySQLStorage;
import com.czhouses.houses.recovery.storage.RecoveryStorage;

public class RecoveryManager {

	private RecoveryStorage storage;
	private Set<Recovery> recoverys;

	public RecoveryManager() {
		this.storage = new RecoverySQLStorage();
		this.recoverys = storage.download();

		Bukkit.getPluginManager().registerEvents(new RecoveryListener(this), HousesCore.getInstance());
	}

	public void add(Recovery recovery) {
		recoverys.add(recovery);
		storage.add(recovery);
	}

	public void remove(Recovery recovery) {
		recoverys.remove(recovery);
		storage.remove(recovery);
	}

	public void update(Recovery recovery) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.update(recovery);
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	public Recovery getRecovery(String name) {
		return recoverys.stream().filter(i -> i.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}

	public Set<Recovery> getRecoverys() {
		return recoverys;
	}

	public void openMenu(Player player, Recovery recovery) {
		List<ItemStack> itemStack = new ArrayList<ItemStack>(recovery.getItemStack());
		itemStack.forEach(i -> {
			ItemMeta itemMeta = i.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if (itemMeta.hasLore()) {
				lore.addAll(itemMeta.getLore());
			}
			lore.add("§aClique para recuperar este item!");
			itemMeta.setLore(lore);
			i.setItemMeta(itemMeta);
		});
		new RecoveryInventory(itemStack, "Recuperar", player);
	}

}
