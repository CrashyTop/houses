package com.czhouses.houses;

public enum SellType {

	CASH("Cash"),
	MONEY("Coins");

	private String name;

	private SellType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
