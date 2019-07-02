package com.czhouses;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.czhouses.api.npc.NPCManager;
import com.czhouses.config.ConfigManager;
import com.czhouses.houses.HouseManager;
import com.czhouses.mysql.MySQL;

import net.milkbowl.vault.economy.Economy;

public class HousesCore extends JavaPlugin {

	private static HousesCore instance;

	public static HousesCore getInstance() {
		return instance;
	}

	private Economy economy;
	private HouseManager houseManager;
	private MySQL mySQL;
	private ConfigManager config;
	private NPCManager npcManager;

	@Override
	public void onEnable() {
		instance = this;

		Bukkit.getScheduler().runTaskLater(this, () -> {
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				saveResource("config.yml", false);
			}

			try {
				this.mySQL = new MySQL();
			} catch (ClassNotFoundException | SQLException e) {
				System.out.println("MySQL server don't connected!");
				setEnabled(false);
				return;
			}

			setupEconomy();

			this.npcManager = new NPCManager();
			this.config = new ConfigManager();
			this.houseManager = new HouseManager(npcManager);
		}, 10);
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return economy != null;
	}

	@Override
	public void onDisable() {
		if (houseManager != null) {
			houseManager.getAnimation().uninitialize();
		}
	}

	public NPCManager getNpcManager() {
		return npcManager;
	}

	public MySQL getMySQL() {
		return mySQL;
	}

	public Economy getEconomy() {
		return economy;
	}

	public HouseManager getHouseManager() {
		return houseManager;
	}

	public ConfigManager getConfigManager() {
		return config;
	}
}
