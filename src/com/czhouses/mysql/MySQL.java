package com.czhouses.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.czhouses.HousesCore;

public class MySQL {

	private Connection connection;

	public MySQL() throws SQLException, ClassNotFoundException {

		String address = HousesCore.getInstance().getConfig().getString("MySQL.Address");
		int port = HousesCore.getInstance().getConfig().getInt("MySQL.Port");
		String database = HousesCore.getInstance().getConfig().getString("MySQL.Database");
		String user = HousesCore.getInstance().getConfig().getString("MySQL.User");
		String pass = HousesCore.getInstance().getConfig().getString("MySQL.Password");

		String url = "jdbc:mysql://" + address + ":" + port + "/" + database + "?autoReconnect=true";
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url, user, pass);
	}

	public Connection getConnection() {
		return this.connection;
	}

}
