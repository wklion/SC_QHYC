package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;

import com.spd.common.Mapping;
import com.spd.common.Mappings;
import com.spd.config.CommonConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MappingFactory {

	private static Mappings mappings ;
	
	private MappingFactory(){
		
	}
	
	public synchronized static Mappings getInstance(){
		if(mappings == null) {
			XStream xStream = new XStream(new DomDriver());
			//解析分三块内容，1.节点
			xStream.alias("Mappings", Mappings.class);
			xStream.alias("Mapping", Mapping.class);
			//2.内容中的List
			xStream.addImplicitCollection(Mappings.class, "mappingList");
			//3.属性信息
//			xStream.aliasAttribute(Mapping.class, "name", "name");
//			xStream.aliasAttribute(Mapping.class, "type", "type");
			try {   
		        FileInputStream ops = new FileInputStream(new File(CommonConfig.MAPPINGCONFIGPATH));   
		        mappings = (Mappings)xStream.fromXML(ops);   
		        ops.close();   
	        } catch (Exception e) {   
	        	LogTool.logger.error(CommonConfig.MAPPINGCONFIGPATH + " 解析失败，详情:" + e.getMessage());
	        }  
		}
		return mappings;
	}
	
}
