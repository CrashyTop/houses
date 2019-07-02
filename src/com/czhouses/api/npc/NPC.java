package com.czhouses.api.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.czhouses.api.LocationUtils;
import com.czhouses.api.npc.reflection.NPCReflection;
import com.czhouses.api.npc.type.NPCType;

public class NPC extends NPCReflection {

	private NPCType type;
	private NPCDefault npcClass;
	private String location;
	private String identifier;
	private String[] name;

	public NPC(NPCType type, String identifier, String location, String... name) {
		super(type);
		this.type = type;
		this.location = location;
		this.identifier = identifier;
		this.name = name;

		this.npcClass = getNpcDefault();

		npcClass.initializeNPC(location, name);
		npcClass.createPacket();
	}

	public NPC(NPCType type, String identifier, Location location, String... name) {
		super(type);
		this.type = type;
		this.location = LocationUtils.toJson(location);
		this.identifier = identifier;
		this.name = name;

		this.npcClass = getNpcDefault();

		npcClass.initializeNPC(this.location, name);
		npcClass.createPacket();
	}

	public NPCDefault getNPC() {
		return npcClass;
	}

	public NPCType getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

	public String identifier() {
		return identifier;
	}

	public int getID() {
		return npcClass.getEntityID();
	}

	public String[] getName() {
		return name;
	}

	public void show(Player player) {
		getNPC().showEntity(player);
	}

	public void destroy(Player player) {
		getNPC().destroyEntity(player);
	}

}
