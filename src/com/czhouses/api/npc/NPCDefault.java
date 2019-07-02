package com.czhouses.api.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NPCDefault {

	public void initializeNPC(String location, String... name);

	public void createPacket();

	public Object getEntity();

	public int getEntityID();

	public void showEntity(Player player);

	public void destroyEntity(Player player);

	public void teleportEntity(Player player, Location location);

}
