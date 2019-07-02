package com.czhouses.api.npc.reflection;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Packet;

public class Reflections {

	public static void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
		}
	}

	public static Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
		}
		return null;
	}

	public static Field getField(Class<?> target, String fieldName) throws Exception {
		Field field = null;
		field = target.getField(fieldName);
		return field;

	}

	public void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendPacketForAllWorld(Packet<?> packet, Location location) {
		for (Player player : location.getWorld().getPlayers()) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}

	}

	public void sendPacketForAll(Packet<?> packet) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}

	}

}
