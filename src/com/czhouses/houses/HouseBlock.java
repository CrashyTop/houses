package com.czhouses.houses;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.czhouses.api.LocationUtils;

public class HouseBlock {

	private String location;
	private int id;
	private short sh;

	@SuppressWarnings("deprecation")
	public HouseBlock(Location location, Block block) {
		this.location = LocationUtils.toJson(location);
		this.id = block.getTypeId();
		this.sh = block.getData();
	}

	public Location getLocation() {
		return LocationUtils.fromJson(location);
	}

	public int getId() {
		return id;
	}

	public short getDurability() {
		return sh;
	}
}
