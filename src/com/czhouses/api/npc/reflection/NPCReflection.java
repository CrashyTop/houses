package com.czhouses.api.npc.reflection;

import com.czhouses.api.npc.NPCDefault;
import com.czhouses.api.npc.type.NPCType;
import com.czhouses.api.npc.type.armorstand.NPCArmorStand;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public class NPCReflection {

	private NPCDefault npcDefault;

	public NPCReflection(NPCType type) {
		this.npcDefault = type.getNpcClass();
	}

	public EntityPlayer getEntity() {

		EntityPlayer entity = null;

		try {
			Class<? extends NPCDefault> npcClass = npcDefault.getClass();
			entity = (EntityPlayer) npcClass.getDeclaredMethod("getEntity").invoke(npcDefault);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	public void setInvisible(boolean arg0) {
		try {
			if (npcDefault instanceof NPCArmorStand) {
				Class<? extends NPCDefault> npcClass = npcDefault.getClass();
				npcClass.getDeclaredMethod("setInvisible", boolean.class).invoke(npcDefault, arg0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setName(String arg0) {
		try {
			if (npcDefault instanceof NPCArmorStand) {
				Class<? extends NPCDefault> npcClass = npcDefault.getClass();
				npcClass.getDeclaredMethod("setName", String.class).invoke(npcDefault, arg0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NPCDefault getNpcDefault() {
		return npcDefault;
	}

}
