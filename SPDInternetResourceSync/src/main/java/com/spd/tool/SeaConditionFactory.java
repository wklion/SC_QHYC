package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;

import com.spd.common.SeaCondition;
import com.spd.common.SeaConditions;
import com.spd.config.CommonConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class SeaConditionFactory {

	private static SeaConditions seaConditions;
	
	private SeaConditionFactory() {
		
	}
	
	public synchronized static SeaConditions getInstance(){
		if(seaConditions == null) {
			XStream xStream = new XStream(new DomDriver());
			//解析分三块内容，1.节点
			xStream.alias("SeaConditions", SeaConditions.class);
			xStream.alias("SeaCondition", SeaCondition.class);
			//2.内容中的List
			xStream.addImplicitCollection(SeaConditions.class, "seaConditions");
			//3.属性信息
			try {   
		        FileInputStream ops = new FileInputStream(new File(CommonConfig.SEACONDITIONCONFIGPATH));   
		        seaConditions = (SeaConditions)xStream.fromXML(ops);   
		        ops.close();   
	        } catch (Exception e) {   
	        	LogTool.logger.error(CommonConfig.SEACONDITIONCONFIGPATH + " 解析失败，详情:" + e.getMessage());
	        }  
		}
		return seaConditions;
	}
	
	public static void main(String[] args) {
		SeaConditions seaConditions = SeaConditionFactory.getInstance();
		System.out.println(seaConditions);
	}
}
