package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;

import com.spd.common.TabTimeDataConfig;
import com.spd.common.TabTimeDataConfigs;
import com.spd.config.CommonConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TabTimeDataConfigFactory {

	private static TabTimeDataConfigs tabTimeDataConfigs ;
	
	private TabTimeDataConfigFactory(){
		
	}
	
	public synchronized static TabTimeDataConfigs getInstance(){
		if(tabTimeDataConfigs == null) {
			XStream xStream = new XStream(new DomDriver());
			//解析分三块内容，1.节点
			xStream.alias("TabTimeDataConfigs", TabTimeDataConfigs.class);
			xStream.alias("TabTimeDataConfig", TabTimeDataConfig.class);
			//2.内容中的List
			xStream.addImplicitCollection(TabTimeDataConfigs.class, "tabTimeDataConfigs");
			//3.属性信息
			try {   
		        FileInputStream ops = new FileInputStream(new File(CommonConfig.TABTIMEDATACONFIGPATH));   
		        tabTimeDataConfigs = (TabTimeDataConfigs)xStream.fromXML(ops);   
		        ops.close();   
	        } catch (Exception e) {   
	        	LogTool.logger.error(CommonConfig.RESOURCECONFIGPATH + " 解析失败，详情:" + e.getMessage());
	        }  
		}
		return tabTimeDataConfigs;
	}
}
