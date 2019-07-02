package com.czhouses.api;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class LocationUtils {

	public static String serializerChunk(Chunk c) {
		return c.getWorld().getName() + ";" + c.getX() + ";" + c.getZ();
	}

	public static String toJson(Location location) {
		String name = location.getWorld().getName();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		float yaw = location.getYaw();
		float pitch = location.getPitch();
		return name + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
	}

	public static String toJsonINT(Location location) {
		String name = location.getWorld().getName();
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		float yaw = location.getYaw();
		float pitch = location.getPitch();
		return name + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
	}

	public static Location fromJson(String json) {
		String name = json.split(";")[0];
		double x = Double.valueOf(json.split(";")[1]);
		double y = Double.valueOf(json.split(";")[2]);
		double z = Double.valueOf(json.split(";")[3]);
		float yaw = Float.parseFloat(json.split(";")[4]);
		float pitch = Float.parseFloat(json.split(";")[5]);

		Location location = new Location(Bukkit.getWorld(name), x, y, z);
		location.setYaw(yaw);
		location.setPitch(pitch);

		return location;
	}

	public static boolean hasLocation(String json) {
		return json.contains(";") && json.split(";")[5] != null;
	}

	public static Location modifyLocation(Location location) {
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		Location l = new Location(location.getWorld(), x + 0.5, y, z + 0.5);
		l.setYaw(location.getYaw());
		l.setPitch(location.getPitch());
		return location;
	}

	public static boolean isEquals(String location, String location2) {
		return isEquals(fromJson(location), fromJson(location2));
	}

	public static boolean isEquals(Location location, Location location2) {
		if (location.getWorld().getName().equalsIgnoreCase(location2.getWorld().getName())) {
			int x = (int) location.getX();
			int y = (int) location.getY();
			int z = (int) location.getZ();

			int x2 = (int) location2.getX();
			int y2 = (int) location2.getY();
			int z2 = (int) location2.getZ();

			if (x == x2) {
				if (y == y2) {
					if (z == z2) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Location getCenter(Location location) {
		// int x = (int) location.getX();
		// int y = (int) location.getY();
		// int z = (int) location.getZ();
		// Location l = new Location(location.getWorld(), x + 0.5, y, z + 0.5);
		// l.setYaw(location.getYaw());
		// l.setPitch(location.getPitch());
		return location;
	}

}
