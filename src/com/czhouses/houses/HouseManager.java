package com.czhouses.houses;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.czhouses.HousesCore;
import com.czhouses.api.Cuboid;
import com.czhouses.api.LocationUtils;
import com.czhouses.api.npc.NPCManager;
import com.czhouses.houses.animation.Animation;
import com.czhouses.houses.commands.CommandHouse;
import com.czhouses.houses.recovery.RecoveryManager;
import com.czhouses.houses.storage.HouseSQLStorage;
import com.czhouses.houses.storage.HouseStorage;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HouseManager {

	private RecoveryManager recoveryManager;
	private HouseStorage storage;
	private Set<House> houses;

	private Animation animation;
	private Location location_hologram;

	public HouseManager(NPCManager npcManager) {
		loadLocation();

		this.recoveryManager = new RecoveryManager();
		this.storage = new HouseSQLStorage();
		this.houses = storage.download();
		this.animation = new Animation(this, npcManager);

		registerCommand(new CommandHouse(this));

		Bukkit.getPluginManager().registerEvents(new HouseListener(this), HousesCore.getInstance());
	}

	public void registerCommand(Command cmd) {
		((CraftServer) Bukkit.getServer()).getCommandMap().register(cmd.getName(), cmd);
	}

	public void add(Cuboid cuboid, String name, double price_sell, SellType sellType, Location entry, int andar,
			Set<HouseBlock> houseBlocks, Location sign, String regionName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				House house = storage.add(cuboid, name, price_sell, sellType, entry, andar, houseBlocks, sign,
						regionName);
				houses.add(house);
				animation.updateHouses();
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	public void remove(House house) {
		houses.remove(house);
		animation.updateHouses();

		new BukkitRunnable() {
			@Override
			public void run() {
				storage.remove(house.getId());
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	public void update(House house) {
		animation.updateHouses();

		new BukkitRunnable() {
			@Override
			public void run() {
				storage.update(house);
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	public House getHouseByID(int id) {
		return houses.stream().filter(i -> i.getId() == id).findAny().orElse(null);
	}

	public House getHouseByOwner(Player player) {
		return getHouseByOwner(player.getName());
	}

	public House getHouseByOwner(String owner) {
		return houses.stream().filter(i -> i.getOwner() != null && i.getOwner().equalsIgnoreCase(owner)).findAny()
				.orElse(null);
	}

	public House getHouseByLocation(Location location) {
		return houses.stream().filter(
				i -> i.getCuboid().contains((int) location.getX(), (int) location.getY(), (int) location.getZ()))
				.findAny().orElse(null);
	}

	public Set<House> getHouses() {
		return houses;
	}

	public RecoveryManager getRecoveryManager() {
		return recoveryManager;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setLocation(Location location) {
		File file = new File(HousesCore.getInstance().getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		config.set("Location_hologram", LocationUtils.toJson(location));

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		location_hologram = location;

		if (animation != null) {
			animation.teleport(location);
		}
	}

	public void loadLocation() {
		File file = new File(HousesCore.getInstance().getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		if (config.getString("Location_hologram") != null) {
			Location fromJson = LocationUtils.fromJson(config.getString("Location_hologram"));
			if (fromJson != null && fromJson.getWorld() != null) {
				location_hologram = fromJson;
				return;
			}
		}

		config.set("Location_hologram", LocationUtils.toJson(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)));

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadLocation();
	}

	public Location getLocationHologram() {
		return location_hologram;
	}

	public Set<ProtectedRegion> getRegions(Location location) {
		WorldGuardPlugin plugin = WGBukkit.getPlugin();
		RegionManager regionManager = plugin.getRegionManager(location.getWorld());
		ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(location);
		Set<ProtectedRegion> regions = applicableRegions.getRegions();
		return regions;
	}

	public void addMemberInRegion(Player player, Location location, String regionName) {
		System.out.println("adding '" + player.getName() + "' in region '" + regionName + "'");
		ProtectedRegion region = getRegions(location).stream().filter(i -> i.getId().equalsIgnoreCase(regionName))
				.findAny().orElse(null);
		if (region != null) {
			region.getMembers().addPlayer(player.getName());

			setEntry(location, regionName, false);
		}
		System.out.println("action completed!");
	}

	public void removeMemberInRegion(String player, Location location, String regionName) {
		ProtectedRegion region = getRegions(location).stream().filter(i -> i.getId().equalsIgnoreCase(regionName))
				.findAny().orElse(null);
		if (region != null) {
			region.getMembers().removePlayer(player);
		}
	}

	public boolean getRegionByName(String regionName) {
		WorldGuardPlugin plugin = WGBukkit.getPlugin();
		return Bukkit.getWorlds().stream().filter(i -> plugin.getRegionManager(i).getRegion(regionName) != null)
				.findAny().orElse(null) != null;
	}

	public void setEntry(Location location, String regionName, boolean entry) {
		ProtectedRegion region = getRegions(location).stream().filter(i -> i.getId().equalsIgnoreCase(regionName))
				.findAny().orElse(null);
		if (region != null) {
			region.setFlag(new StateFlag("entry", false), entry ? StateFlag.State.ALLOW : StateFlag.State.DENY);
		}

	}
}
