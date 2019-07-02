package com.czhouses.api.npc.type.armorstand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import com.czhouses.api.LocationUtils;
import com.czhouses.api.npc.NPCDefault;
import com.czhouses.api.npc.reflection.Reflections;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPCArmorStand extends Reflections implements NPCDefault {

	private EntityArmorStand entity;
	private String name[];
	private String location;
	private int entityID;

	@Override
	public void initializeNPC(String location, String... name) {
		this.location = location;
		this.name = name;
	}

	@Override
	public void createPacket() {
		Location location = LocationUtils.fromJson(this.location);
		Location center = LocationUtils.getCenter(location);

		WorldServer worldServer = ((CraftWorld) center.getWorld()).getHandle();

		this.entity = new EntityArmorStand(worldServer);

		entity.setLocation(center.getX(), center.getY(), center.getZ(), center.getYaw(), center.getPitch());
		entity.setCustomNameVisible(true);
		entity.setCustomName(name[0]);

		this.entityID = entity.getId();
	}

	@Override
	public Object getEntity() {
		return entity;
	}

	@Override
	public void showEntity(Player player) {
		sendPacket(new PacketPlayOutSpawnEntityLiving(entity), player);
	}

	@Override
	public void destroyEntity(Player player) {
		sendPacket(new PacketPlayOutEntityDestroy(entityID), player);
	}

	@Override
	public int getEntityID() {
		return entityID;
	}

	@Override
	public void teleportEntity(Player player, Location location) {
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);

		entity.locX = location.getX();
		entity.locY = location.getY();
		entity.locZ = location.getZ();
		entity.yaw = location.getYaw();
		entity.pitch = location.getPitch();

		sendPacket(packet, player);
	}

	public void setInvisible(boolean arg0) {
		entity.setInvisible(arg0);
	}

	public void setName(String name) {
		entity.setCustomName(name);

		Bukkit.getOnlinePlayers().forEach(
				player -> sendPacket(new PacketPlayOutEntityMetadata(entityID, entity.getDataWatcher(), true), player));
	}

}
