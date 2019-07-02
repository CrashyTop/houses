package com.czhouses.api.npc.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.czhouses.HousesCore;
import com.czhouses.api.npc.NPCManager;

public class NPCListener implements Listener {

	private NPCManager npcManager;

	public NPCListener(NPCManager npcManager) {
		this.npcManager = npcManager;
		Bukkit.getPluginManager().registerEvents(this, HousesCore.getInstance());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskAsynchronously(HousesCore.getInstance(), () -> npcManager.loadNPCs(player));
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(HousesCore.getInstance(), () -> npcManager.loadNPCs(player));
	}

}
