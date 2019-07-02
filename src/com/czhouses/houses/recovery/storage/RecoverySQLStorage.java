package com.czhouses.houses.recovery.storage;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;

import com.czhouses.HousesCore;
import com.czhouses.houses.recovery.Recovery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RecoverySQLStorage implements RecoveryStorage {

	private Connection connection;

	public RecoverySQLStorage() {
		connection = HousesCore.getInstance().getMySQL().getConnection();
		try {
			Statement st = connection.createStatement();
			st.execute("CREATE TABLE IF NOT EXISTS `recovery` (`name` varchar(16), `items` text);");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<Recovery> download() {
		Set<Recovery> recoverys = new HashSet<Recovery>();

		Type type = new TypeToken<Map<Integer, String>>() {
		}.getType();

		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM `recovery`");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				Map<Integer, String> items = new Gson().fromJson(rs.getString("items"), type);

				recoverys.add(new Recovery(name, items));
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recoverys;
	}

	@Override
	public void add(Recovery recovery) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = connection
							.prepareStatement("INSERT INTO `recovery` (`name`, `items`) VALUES (?,?)");
					ps.setString(1, recovery.getName());
					ps.setString(2, new Gson().toJson(recovery.getItems()));
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	@Override
	public void remove(Recovery recovery) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = connection.prepareStatement("DELETE FROM `recovery` WHERE `name`=?");
					ps.setString(1, recovery.getName());
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

	@Override
	public void update(Recovery recovery) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = connection
							.prepareStatement("UPDATE `recovery` SET `items`=? WHERE `name`=?");
					ps.setString(1, new Gson().toJson(recovery.getItems()));
					ps.setString(2, recovery.getName());
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

}
