package com.spd.qhyc.model;
/**
 * @作者:wangkun
 * @日期:2017年11月2日
 * @公司:spd
 * @说明:
*/
public class BaseDataBase {
	private String server;
	private String port;
	private String database;
	private String user;
	private String password;
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
