package com.czhouses.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Item extends ItemStack {

	public Item(Material material) {
		super(material);
	}

	public Item(ItemStack itemstack) {
		super(itemstack);
	}

	public Item setAmounts(int amount) {
		setAmount(amount);
		return this;
	}

	public Item setDisplayName(String name) {
		ItemMeta itemMeta = getItemMeta();
		itemMeta.setDisplayName(name);
		setItemMeta(itemMeta);
		return this;
	}

	public Item setData(int data) {
		setData(data);
		return this;
	}

	public Item setLore(String... lore) {
		ItemMeta itemMeta = getItemMeta();
		itemMeta.setLore(Arrays.asList(lore));
		setItemMeta(itemMeta);
		return this;
	}

	public Item setLore(List<String> lore) {
		ItemMeta itemMeta = getItemMeta();
		itemMeta.setLore(lore);
		setItemMeta(itemMeta);
		return this;
	}

	public Item setOwner(String owner) {
		SkullMeta skullMeta = (SkullMeta) getItemMeta();
		skullMeta.setOwner(owner);
		setItemMeta(skullMeta);
		return this;
	}

	public Item setEnchantmentBook(Enchantment enchantment) {
		EnchantmentStorageMeta enc = (EnchantmentStorageMeta) getItemMeta();
		enc.addStoredEnchant(enchantment, 1, true);
		setItemMeta(enc);
		return this;
	}

	public Item setDurabilityy(short s) {
		this.setDurability(s);
		return this;
	}

	public Item addItemFlag(ItemFlag... flags) {
		ItemMeta itemMeta = getItemMeta();
		itemMeta.addItemFlags(flags);
		setItemMeta(itemMeta);
		return this;
	}

	public List<String> getLore() {
		return getItemMeta().getLore();
	}
}
