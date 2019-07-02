package com.czhouses.houses.recovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.inventory.ItemStack;

import com.czhouses.api.InventoryUtils;

public class Recovery {

	private String name;
	private Map<Integer, String> items;

	private long expire;

	public Recovery(String name, Map<Integer, String> items) {
		this.name = name;
		this.items = items;

		this.expire = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
	}

	public String getName() {
		return name;
	}

	public Map<Integer, String> getItems() {
		return items;
	}

	public List<ItemStack> getItemStack() {
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();
		items.values().stream().forEach(i -> {
			ItemStack stringToItemStack = InventoryUtils.stringToItemStack(i);
			itemStacks.add(stringToItemStack);
		});
		return itemStacks;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= expire;
	}

	public long getExpire() {
		return expire;
	}

}
