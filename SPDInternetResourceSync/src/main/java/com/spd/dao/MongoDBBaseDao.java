package com.spd.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoDBBaseDao {

	public DBObject findOne(String objectName, String columnName, Object value) {
		try {
			DB db = MongoConnFactory.getDB();
			DBCollection dbConllection = db.getCollection(objectName);
			DBObject obObject = dbConllection.findOne(new BasicDBObject(columnName, value));
			return obObject;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<DBObject> findArray(String objectName, String columnName, Object value) {
		List<DBObject> resultList = new ArrayList<DBObject>();
		try {
			DB db = MongoConnFactory.getDB();
			DBCollection dbConllection = db.getCollection(objectName);
			DBCursor dbCursor = dbConllection.find(new BasicDBObject(columnName, value));
			while(dbCursor.hasNext()) {
				DBObject dbObject = dbCursor.next();
				resultList.add(dbObject);
			}
			return resultList;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
