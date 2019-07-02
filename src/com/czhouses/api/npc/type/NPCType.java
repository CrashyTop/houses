package com.czhouses.api.npc.type;

import com.czhouses.api.npc.NPCDefault;
import com.czhouses.api.npc.type.armorstand.NPCArmorStand;

public enum NPCType {

	ARMOR_STAND(new NPCArmorStand(), 1);

	private NPCDefault npcDefault;
	private double height;

	private NPCType(NPCDefault npcDefault, double height) {
		this.npcDefault = npcDefault;
		this.height = height;
	}

	public NPCDefault getNpcClass() {
		try {
			return npcDefault.getClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public double getHeight() {
		return height;
	}

}
