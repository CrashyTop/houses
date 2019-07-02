package com.czhouses.houses;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.czhouses.HousesCore;
import com.czhouses.api.Cuboid;
import com.czhouses.api.InventoryUtils;
import com.czhouses.api.LocationUtils;
import com.czhouses.config.ConfigManager;
import com.czhouses.houses.recovery.Recovery;
import com.czhouses.houses.recovery.RecoveryManager;

public class House {

	private int id;
	private Cuboid cuboid;
	private String name;
	private double price_sell;

	private String owner = "";
	private long date_buy;
	private int days;
	private SellType sellType;
	private String entry;
	private int andar;
	private Set<HouseBlock> houseBlocks;
	private String sign;
	private String regionName;

	public House(int id, Cuboid cuboid, String name, double price_sell, SellType sellType, Location entry, int andar,
			Set<HouseBlock> houseBlocks, Location sign, String regionName, String owner, int days, long date_buy) {
		this.id = id;
		this.cuboid = cuboid;
		this.name = name;
		this.price_sell = price_sell;
		this.sellType = sellType;
		this.andar = andar;
		this.houseBlocks = houseBlocks;
		this.regionName = regionName;
		this.sign = LocationUtils.toJson(sign);
		this.entry = LocationUtils.toJson(entry);
		this.owner = owner;

		this.days = days;
		this.date_buy = date_buy;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Cuboid getCuboid() {
		return cuboid.clone();
	}

	public String getName() {
		return name;
	}

	public double getPriceSell() {
		return price_sell;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean hasOwner() {
		return !(owner == null || owner == "" || owner == " ");
	}

	public long getDateExpire() {
		return date_buy + TimeUnit.DAYS.toMillis(days);
	}

	public long getDateBuy() {
		return date_buy;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public void setDateBuy(long date_buy) {
		this.date_buy = date_buy;
	}

	public SellType getSellType() {
		return sellType;
	}

	public Location getEntry() {
		return LocationUtils.fromJson(entry);
	}

	public Location getSign() {
		return LocationUtils.fromJson(sign);
	}

	public int getAndar() {
		return andar;
	}

	public Set<HouseBlock> getHouseBlocks() {
		return houseBlocks;
	}

	public String getRegionName() {
		return regionName;
	}

	@SuppressWarnings("deprecation")
	public void deleteOwner() {
		Map<Integer, String> items = new HashMap<Integer, String>();

		int iditemstack = 0;
		for (Block i : cuboid.getBlocks()) {
			if (i.getType() == Material.CHEST) {
				Chest chest = (Chest) i.getState();

				Inventory inv = chest.getInventory();
				ItemStack[] contents = inv.getContents().clone();
				inv.clear();

				for (ItemStack itemStack : contents) {
					if (itemStack != null) {
						items.put(++iditemstack, InventoryUtils.itemstackToString(itemStack.clone()));
					}
				}
			}
		}

//		itemsSet.entrySet().forEach(
//				i -> items.add(InventoryUtils.itemstackToString(new Item(i.getKey()).setAmounts(i.getValue()))));

		cuboid.getLocations().stream().forEach(i -> {
			if (i.getBlock() != null) {
				i.getBlock().setType(Material.AIR);
			}
		});

		Location center = cuboid.getCenter();
		double radius = Math.abs(center.getZ()) - Math.abs(cuboid.getMaxZ());
		center.getWorld().getNearbyEntities(center, radius, radius, radius).forEach(i -> {
			if (i.getType() == EntityType.PAINTING || i.getType() == EntityType.ITEM_FRAME) {
				i.remove();
			}
		});

		HouseManager houseManager = HousesCore.getInstance().getHouseManager();
		RecoveryManager recoveryManager = houseManager.getRecoveryManager();

		if (recoveryManager.getRecovery(owner) != null) {
			Recovery recovery = recoveryManager.getRecovery(owner);
			for (Entry<Integer, String> entry2 : items.entrySet()) {
				int r = new Random().nextInt(1293812);
				recovery.getItems().put(r, entry2.getValue());
			}
			recoveryManager.update(recovery);
		} else {
			Recovery recovery = new Recovery(owner, items);
			recoveryManager.add(recovery);
		}

		houseBlocks.stream()
				.forEach(i -> i.getLocation().getBlock().setTypeIdAndData(i.getId(), (byte) i.getDurability(), true));

		ConfigManager configManager = HousesCore.getInstance().getConfigManager();
		if (sellType == SellType.CASH) {
			for (String command : configManager.getCashDisable()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", owner));
			}
		} else {
			for (String command : configManager.getMoneyDisable()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", owner));
			}
		}

		houseManager.removeMemberInRegion(owner, houseBlocks.stream().findFirst().get().getLocation(), regionName);
		houseManager.setEntry(houseBlocks.stream().findFirst().get().getLocation(), regionName, true);

		this.owner = "";
		this.days = 0;
		this.date_buy = 0l;
	}
}
