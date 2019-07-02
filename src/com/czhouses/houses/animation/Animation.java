package com.czhouses.houses.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.czhouses.HousesCore;
import com.czhouses.api.npc.NPC;
import com.czhouses.api.npc.NPCManager;
import com.czhouses.api.npc.type.NPCType;
import com.czhouses.houses.House;
import com.czhouses.houses.HouseManager;
import com.czhouses.houses.SellType;

public class Animation {

	private List<House> houses;
	private HouseManager houseManager;

	private Map<Integer, Integer> armorStands;
	private Integer line0;
	private NPCManager npcManager;

	public Animation(HouseManager houseManager, NPCManager npcManager) {
		this.houseManager = houseManager;
		this.npcManager = npcManager;
		this.houses = new ArrayList<House>();

		this.armorStands = new HashMap<Integer, Integer>();

		updateHouses();

		Location locationHologram = houseManager.getLocationHologram();
		if (locationHologram != null) {
			initialize(locationHologram);
		}

		Bukkit.getPluginManager().registerEvents(new AnimationListener(houseManager), HousesCore.getInstance());
	}

	public void teleport(Location location) {
		uninitialize();
		initialize(location);
	}

	public void uninitialize() {

		if (armorStands != null) {
			armorStands.values().stream().forEach(i -> Bukkit.getOnlinePlayers().forEach(player -> {
				npcManager.getNpcByID(i).destroy(player);
				if (line0 != null) {
					npcManager.getNpcByID(line0).destroy(player);
				}
			}));
		}

		armorStands = null;
		line0 = null;
	}

	public void updateHouses() {
		houses.clear();
		houses.addAll(houseManager.getHouses().stream().filter(i -> !i.hasOwner()).collect(Collectors.toList()));
	}

	private House[] hous = new House[] { null, null, null, null, null, null, null, null, null, null };
	private int now = 9;

	public void onSwitch() {
		if (armorStands == null) {
			return;
		}
		String loading = "§e...";

		if (houses.size() > 10) {
			now += 1;
			int n = now >= houses.size() ? now = 0 : now;

			for (int x = 0; x < 10; x++) {
				hous[x] = x >= 9 ? houses.get(n) : hous[x + 1];

				NPC am = npcManager.getNpcByID(armorStands.get(x));
				if (am == null) {
					continue;
				}
				am.setName(hous[x] == null ? loading
						: "§e[" + hous[x].getId() + "]" + hous[x].getName() + " - " + hous[x].getPriceSell() + " "
								+ (hous[x].getSellType() == SellType.CASH ? "§6" : "§2")
								+ hous[x].getSellType().getName() + "§e - §aDisponível");
			}
		} else {
			for (int x = 0; x < 10; x++) {
				NPC am = npcManager.getNpcByID(armorStands.get(Math.abs(10 - x) - 1));
				if (am == null) {
					continue;
				}
				am.setName(x >= houses.size() || houses.get(x) == null ? loading
						: "§e[" + houses.get(x).getId() + "]" + houses.get(x).getName() + " - "
								+ houses.get(x).getPriceSell() + " "
								+ (houses.get(x).getSellType() == SellType.CASH ? "§6" : "§2")
								+ houses.get(x).getSellType().getName() + "§e - §aDisponível");
			}
		}
	}

	public void initialize(Location location) {
		this.armorStands = new HashMap<Integer, Integer>();
		try {
			for (int x = 0; x < 10; x++) {
				hous[x] = houses.size() > x ? houses.get(x) : null;
				armorStands.put(x, genArmor(location.add(0, 0.3, 0), "§e..."));
			}
			line0 = genArmor(location.add(0, 0.3, 0), "§e§lLISTA DE CASAS PARA ALUGAR!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houses animation: error on try initialize animation!");
		}
	}

	public int genArmor(Location loc, String name) {
		NPC npc = new NPC(NPCType.ARMOR_STAND, "npc", loc, name);
		npc.setInvisible(true);
		npcManager.registerNPC(npc);
		Bukkit.getOnlinePlayers().forEach(player -> npc.getNPC().showEntity(player));
		return npc.getID();
	}

//	public ArmorStand genArmor(Location loc) {
//		Location location = loc.clone();
//		ArmorStand am = location.getWorld().spawn(location, ArmorStand.class);
//		am.setCustomNameVisible(true);
//		am.setCustomName("§e...");
//		am.setGravity(false);
//		am.setVisible(false);
//		am.setMetadata("animation", new FixedMetadataValue(HousesCore.getInstance(), "animation"));
//		return am;
//	}
}
