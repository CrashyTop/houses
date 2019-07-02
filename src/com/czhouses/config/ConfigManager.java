package com.czhouses.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.czhouses.HousesCore;

public class ConfigManager {

	private List<String> cashEnable;
	private List<String> cashDisable;
	private List<String> moneyEnable;
	private List<String> moneyDisable;

	public ConfigManager() {
		this.cashEnable = new ArrayList<String>();
		this.cashDisable = new ArrayList<String>();
		this.moneyEnable = new ArrayList<String>();
		this.moneyDisable = new ArrayList<String>();

		load();
	}

	public void load() {
		File file = new File(HousesCore.getInstance().getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		if (config.getString("Commands") != null) {
			cashEnable.addAll(config.getStringList("Commands.Cash.Enable"));
			cashDisable.addAll(config.getStringList("Commands.Cash.Disable"));
			moneyEnable.addAll(config.getStringList("Commands.Money.Enable"));
			moneyDisable.addAll(config.getStringList("Commands.Money.Disable"));
		}
	}

	public List<String> getCashDisable() {
		return cashDisable;
	}

	public List<String> getCashEnable() {
		return cashEnable;
	}

	public List<String> getMoneyDisable() {
		return moneyDisable;
	}

	public List<String> getMoneyEnable() {
		return moneyEnable;
	}
}
