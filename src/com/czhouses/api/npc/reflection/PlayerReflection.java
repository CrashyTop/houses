package com.czhouses.api.npc.reflection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerReflection {

	public static String replaceVariables(String text, Player player) {

		text = text.replace("%player%", player.getName());
		text = text.replace("%ping%", getPing(player) + "");
		text = text.replace("%online%", Bukkit.getOnlinePlayers().size() + "");
		text = text.replace("%maxplayers%", Bukkit.getServer().getMaxPlayers() + "");

		return text;
	}

	public static int getPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}

	public static void replace(String text) {

		String bla = text;

		// if (bla.contains("%player%")) {
		bla.replace("%player%", "jaksomgamer");
		// }

		if (bla.contains("%online%")) {
			bla.replace("%online%", "1");
		}

		System.out.println(bla);

	}

	public static void main(String[] args) {

		String buildString = buildString("jaksomgamer", "teste1");

		// String replace = replace("Bem vindo %player% atualmente %online%");
		System.out.println(buildString);
	}

	public static String buildString(Object... obj) {
		String msg = "Bem vindo %player% atualmente %online%";
		// for (int i = 0; i < obj.length; i++) {
		msg = msg.replace("%player%", obj[0].toString());
		// }
		return msg;
	}

}
