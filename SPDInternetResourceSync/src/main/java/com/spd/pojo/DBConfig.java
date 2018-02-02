package com.spd.pojo;

import java.util.*;

/**
 * 
 * 创建日期:2012-6-28
 * Title:文件所属模块
 * Description：数据库连接信息配置

 * @author 宁利广
 * @mender：（文件的修改者，文件创建者之外的人）
 * @version 1.0
 * Remark：认为有必要的其他信息
 */
public class DBConfig {
/*	sppm.conn.dialect=org.hibernate.dialect.SQLServerDialect
	sppm.conn.url=jdbc:sqlserver://SUPERMAP-PC;DatabaseName=sppm;user=sa;password=cmdes
	sppm.conn.username=sa
	sppm.conn.password=cmdes
	sppm.conn.driver_class =com.microsoft.sqlserver.jdbc.SQLServerDriver*/
	//业务对应的数据库连接名称

	
	private String connName;
	//数据库连接
	private String  ip;
	
	private int port;
	
	private String user;
	
	private String password;

	private String db;
	
	private String driverClass;

	private int minpoolsize;
	
	private int maxpoolsize;
	
	private int maxstatements;
	
	private int maxidletime;
	
	public int getMinpoolsize() {
		return minpoolsize;
	}

	public void setMinpoolsize(int minpoolsize) {
		this.minpoolsize = minpoolsize;
	}

	public int getMaxpoolsize() {
		return maxpoolsize;
	}

	public void setMaxpoolsize(int maxpoolsize) {
		this.maxpoolsize = maxpoolsize;
	}

	public int getMaxstatements() {
		return maxstatements;
	}

	public void setMaxstatements(int maxstatements) {
		this.maxstatements = maxstatements;
	}

	public int getMaxidletime() {
		return maxidletime;
	}

	public void setMaxidletime(int maxidletime) {
		this.maxidletime = maxidletime;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public DBConfig(String connName) {
		this.connName = connName;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
