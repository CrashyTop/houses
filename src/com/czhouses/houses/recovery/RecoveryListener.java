package com.czhouses.houses.recovery;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RecoveryListener implements Listener {

	private RecoveryManager recoveryManager;

	public RecoveryListener(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}

	@EventHandler
	public void onInv(InventoryClickEvent event) {
		new RecoveryInventory(event, recoveryManager);
	}

}
