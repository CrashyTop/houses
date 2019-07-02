package com.czhouses.api.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.czhouses.api.npc.listener.NPCListener;

public class NPCManager {

	private Map<Integer, NPC> npcs;
	private Set<String> npcName;

	public NPCManager() {
		this.npcs = new HashMap<Integer, NPC>();
		this.npcName = new HashSet<String>();
		new NPCListener(this);
	}

	public void registerNPC(NPC npc) {
		npcName.add(npc.identifier());
		this.npcs.put(npc.getID(), npc);
	}

	public void unregisterNPC(NPC npc) {
		this.npcName.remove(npc.identifier());
		this.npcs.remove(npc.getID());
	}

	public void unregisterNPC(int npcID) {
		NPC npc = getNpcByID(npcID);
		this.npcName.remove(npc.identifier());
		this.npcs.remove(npc.getID());
	}

	public void loadNPCs(Player player) {
		npcs.values().forEach(i -> {
			i.getNPC().showEntity(player);
		});
	}

	public NPC getNpcByID(int ID) {
		return npcs.get(ID);
	}

	public Map<Integer, NPC> getNpcs() {
		return npcs;
	}

	public Set<String> getNpcName() {
		return npcName;
	}

}
