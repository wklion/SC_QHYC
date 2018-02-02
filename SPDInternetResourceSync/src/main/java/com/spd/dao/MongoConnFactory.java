package com.spd.dao;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.spd.config.CommonConfig;
import com.spd.pojo.DBConfig;
import com.spd.tool.PropertiesUtil;

public class MongoConnFactory {
	
	private static MongoClient mongoClient = null;

	@SuppressWarnings("deprecation")
	public static  DB getDB() throws UnknownHostException {
		
//		PropertiesUtil.loadSysCofing();
		List dbConfigList = CommonConfig.DBConn.DB_CONFIG_HOLDER;
		DBConfig dbConfig = null;//(DBConfig) dbConfigList.get(0);
		for(int i=0; i<dbConfigList.size(); i++) {
			DBConfig item = (DBConfig) dbConfigList.get(i);
			String connName = item.getConnName();
			if(connName.equals(CommonConfig.DBConn.DB_SPPM)) {
				dbConfig = item;
				break;
			}
		}
		 DB conn = null;
		 if(mongoClient == null){
			 mongoClient = new MongoClient( dbConfig.getIp() , dbConfig.getPort());
		}
		 String dbName = dbConfig.getDb();
		 String username = dbConfig.getUser();
		 String password = dbConfig.getPassword();
		 conn = mongoClient.getDB(dbName);
//		 conn.authenticate(username, password.toCharArray());
		 return conn;
			
		
	}


	public  static synchronized void closeConnection(){
		
		if(mongoClient != null){
		    mongoClient.close();
		}
	}
		
}