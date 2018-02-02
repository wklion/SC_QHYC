package com.spd.test;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.spd.dao.MongoConnFactory;
import com.spd.tool.PropertiesUtil;

public class MongoDBTest {

	public void update() {
		try {
			PropertiesUtil.loadSysCofing();
			DB db = MongoConnFactory.getDB();
			DBCollection dbCollection = db.getCollection("t_doc");
			DBCursor cursor = dbCollection.find();
			Iterator<DBObject> it = cursor.iterator();
			while(it.hasNext()) {
				DBObject dbObject = it.next();
				DBObject updateDBObject = new BasicDBObject();
				Set<String> keySet = dbObject.keySet();
				Iterator<String> keyIt = keySet.iterator();
				while(keyIt.hasNext()) {
					String key = keyIt.next();
					Object value = dbObject.get(key);
					updateDBObject.put(key, value);
				}
				String savePath = (String)dbObject.get("savepath");
				if(savePath.startsWith("/home/soft/apache-tomcat-7.0.59/webapps/images")) {
//					System.out.println("before:" + savePath);
					savePath = savePath.replaceFirst("/home/soft/apache-tomcat-7.0.59/webapps", "/DATA");
					updateDBObject.put("savepath", savePath);
					dbCollection.update(dbObject, updateDBObject);
//					System.out.println("after:" + savePath);
				}
			}
//			DBObject dbObject = dbCollection.findOne();
//			System.out.println(dbObject);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void query() {
		try {
			PropertiesUtil.loadSysCofing();
			DB db = MongoConnFactory.getDB();
			DBCollection dbCollection = db.getCollection("t_doc");
			DBCursor cursor = dbCollection.find();
			Iterator<DBObject> it = cursor.iterator();
			while(it.hasNext()) {
				DBObject dbObject = it.next();
				String savePath = (String)dbObject.get("savepath");
				System.out.println(savePath);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MongoDBTest().query();
	}

}
