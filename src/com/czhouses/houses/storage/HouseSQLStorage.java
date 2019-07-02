package com.czhouses.houses.storage;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.czhouses.HousesCore;
import com.czhouses.api.Cuboid;
import com.czhouses.api.LocationUtils;
import com.czhouses.houses.House;
import com.czhouses.houses.HouseBlock;
import com.czhouses.houses.SellType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HouseSQLStorage implements HouseStorage {

	private Connection connection;

	public HouseSQLStorage() {
		connection = HousesCore.getInstance().getMySQL().getConnection();
		try {
			Statement st = connection.createStatement();
			st.execute(
					"CREATE TABLE IF NOT EXISTS `houses` (`id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT, `cuboid` text, `name` text, `price_sell` bigint, `sell_type` varchar(20), `entry` text, `andar` int(5), `house_blocks` text(100000), `sign` text, `region_name` text, `owner` varchar(16), `days` int, `date_buy` bigint);");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<House> download() {
		Set<House> houses = new HashSet<House>();

		Type type = new TypeToken<Set<HouseBlock>>() {
		}.getType();

		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM `houses`");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				Cuboid cuboid = new Gson().fromJson(rs.getString("cuboid"), Cuboid.class);
				String name = rs.getString("name");
				long price_sell = rs.getLong("price_sell");
				SellType selltype = SellType.valueOf(rs.getString("sell_type"));
				Location entry = LocationUtils.fromJson(rs.getString("entry"));
				int andar = rs.getInt("andar");
				Set<HouseBlock> houseblocks = new Gson().fromJson(rs.getString("house_blocks"), type);
				Location sign = LocationUtils.fromJson(rs.getString("sign"));
				String regionName = rs.getString("region_name");
				String owner = rs.getString("owner");
				int days = rs.getInt("days");
				long date_buy = rs.getLong("date_buy");

				houses.add(new House(id, cuboid, name, price_sell, selltype, entry, andar, houseblocks, sign,
						regionName, owner, days, date_buy));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return houses;
	}

	@Override
	public House add(Cuboid cuboid, String name, double price_sell, SellType sellType, Location entry, int andar,
			Set<HouseBlock> houseBlocks, Location sign, String region_name) {

		try {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO `houses` (`cuboid`, `name`, `price_sell`, `sell_type`, `entry`, `andar`, `house_blocks`, `sign`, `region_name`, `owner`) VALUES (?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, new Gson().toJson(cuboid));
			ps.setString(2, name);
			ps.setDouble(3, price_sell);
			ps.setString(4, sellType.toString());
			ps.setString(5, LocationUtils.toJson(entry));
			ps.setInt(6, andar);
			ps.setString(7, new Gson().toJson(houseBlocks));
			ps.setString(8, LocationUtils.toJson(sign));
			ps.setString(9, region_name);
			ps.setString(10, "");
			ps.executeUpdate();
			ps.close();

			PreparedStatement ps2 = connection
					.prepareStatement("SELECT * FROM `houses` WHERE `name`=? AND `price_sell`=? AND `sell_type`=?");
			ps2.setString(1, name);
			ps2.setDouble(2, price_sell);
			ps2.setString(3, sellType.toString());
			ResultSet rs2 = ps2.executeQuery();

			int id = 0;
			while (rs2.next()) {
				id = rs2.getInt("id");
				break;
			}
			rs2.close();
			ps2.close();

			return new House(id, cuboid, name, price_sell, sellType, entry, andar, houseBlocks, sign, region_name, "",
					0, 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(House house) {

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = connection.prepareStatement(
							"UPDATE `houses` SET `cuboid`=?, `name`=?, `price_sell`=?, `sell_type`=?, `entry`=?, `andar`=?, `house_blocks`=?, `sign`=?, `region_name`=?, `owner`=?, `days`=?, `date_buy`=? WHERE `id`=?");
					ps.setString(1, new Gson().toJson(house.getCuboid()));
					ps.setString(2, house.getName());
					ps.setDouble(3, house.getPriceSell());
					ps.setString(4, house.getSellType().toString());
					ps.setString(5, LocationUtils.toJson(house.getEntry()));
					ps.setInt(6, house.getAndar());
					ps.setString(7, new Gson().toJson(house.getHouseBlocks()));
					ps.setString(8, LocationUtils.toJson(house.getSign()));
					ps.setString(9, house.getRegionName());
					ps.setString(10, house.getOwner() == null ? "" : house.getOwner());
					ps.setInt(11, house.getDays());
					ps.setLong(12, house.getDateBuy());
					ps.setInt(13, house.getId());
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(HousesCore.getInstance());

	}

	@Override
	public void remove(int id) {

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = connection.prepareStatement("DELETE FROM `houses` WHERE `id`=?");
					ps.setInt(1, id);
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(HousesCore.getInstance());
	}

}
