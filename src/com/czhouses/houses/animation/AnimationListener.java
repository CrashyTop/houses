package com.czhouses.houses.animation;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.czhouses.houses.HouseManager;

public class AnimationListener implements Listener {

	private HouseManager houseManager;

	public AnimationListener(HouseManager houseManager) {
		this.houseManager = houseManager;
	}

	@EventHandler
	public void onChunk(ChunkUnloadEvent event) {
		Location locationHologram = houseManager.getLocationHologram();
		if (locationHologram != null) {
			Chunk chunk = event.getChunk();

			int x = chunk.getX();
			int z = chunk.getZ();

			if (x == locationHologram.getChunk().getX() && z == locationHologram.getChunk().getZ()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onArmorStand(PlayerArmorStandManipulateEvent event) {
		ArmorStand armorStand = event.getRightClicked();
		if (armorStand.getMetadata("animation") != null) {
			if (armorStand.hasMetadata("animation")) {
				if (armorStand.getMetadata("animation").get(0) != null) {
					event.setCancelled(true);
				}
			}
		}
	}
}
