package com.czhouses.houses.storage;

import java.util.Set;

import org.bukkit.Location;

import com.czhouses.api.Cuboid;
import com.czhouses.houses.House;
import com.czhouses.houses.HouseBlock;
import com.czhouses.houses.SellType;

public interface HouseStorage {

	public Set<House> download();

	public House add(Cuboid cuboid, String name, double price_sell, SellType sellType, Location entry, int andar,
			Set<HouseBlock> houseBlocks, Location sign, String regionName);

	public void update(House house);

	public void remove(int id);

}
