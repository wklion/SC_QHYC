package com.spd.dao.impl;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.spd.dao.MongoConnFactory;
import com.spd.dao.MongoDBBaseDao;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;

public class InternetImgDao extends MongoDBBaseDao {
	
	public static void main(String[] args) {
		try {
			PropertiesUtil.loadSysCofing();
			DB db = MongoConnFactory.getDB();
			DBCollection dbCollection = db.getCollection("t_doc");
			DBObject dbObject = dbCollection.findOne();
			System.out.println(dbObject);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断图片是否已经下载
	 * @param md5
	 * @return
	 */
	public boolean isPicDownload(String md5) {
		try {
			DB db = MongoConnFactory.getDB();
			DBCollection dbCollection = db.getCollection("t_doc");
			DBObject dbObject = dbCollection.findOne(new BasicDBObject("md5", md5));
			return dbObject == null ? false : true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断图片类型是否已经存在
	 * @param picTypeID
	 * @return
	 */
	public boolean isPicTypeExist(int picTypeID) {
		try {
			DB db = MongoConnFactory.getDB();
			DBCollection dbCollection = db.getCollection("t_doc_type");
			DBObject dbObject = dbCollection.findOne(new BasicDBObject("typeid", picTypeID));
			return dbObject == null ? false : true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 添加图片类型的一条记录
	 * @param mapData
	 */
	public int insertPicType(Map<String, Object> mapData) {
		try {
			DB db = MongoConnFactory.getDB();
			DBObject pictype = new BasicDBObject();
			Set<String> set = mapData.keySet();
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				Object value = mapData.get(key);
				pictype.put(key, value);
			}
			return db.getCollection("t_doc_type").save(pictype).getN();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 插入一张图片
	 * @param mapData
	 */
	public int insertPic(Map<String, Object> mapData) {
		try {
			DB db = MongoConnFactory.getDB();
			DBObject pictype = new BasicDBObject();
			Set<String> set = mapData.keySet();
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				Object value = mapData.get(key);
				pictype.put(key, value);
			}
			int result = db.getCollection("t_doc").save(pictype).getN();
			LogTool.logger.info(mapData.get("filename") + "入库");
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
