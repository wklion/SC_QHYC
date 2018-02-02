package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;

import com.spd.common.Ship;
import com.spd.config.CommonConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ShipFactory {

	private static Ship ship ;
	
	private ShipFactory(){
		
	}
	
	public synchronized static Ship getInstance(){
		if(ship == null) {
			XStream xStream = new XStream(new DomDriver());
			//解析分三块内容，1.节点
			xStream.alias("Ship", Ship.class);
			//2.内容中的List
//			xStream.addImplicitCollection(Ship.class, "tabTimeDataConfigs");
			//3.属性信息
			try {   
		        FileInputStream ops = new FileInputStream(new File(CommonConfig.SHPCONFIGPATH));   
		        ship = (Ship)xStream.fromXML(ops);   
		        ops.close();   
	        } catch (Exception e) {   
	        	LogTool.logger.error(CommonConfig.SHPCONFIGPATH + " 解析失败，详情:" + e.getMessage());
	        }  
		}
		return ship;
	}
	
}
