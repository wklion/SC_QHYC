package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;

import com.spd.common.SeaCondition;
import com.spd.common.SeaConditions;
import com.spd.common.WebServiceConfig;
import com.spd.common.Webservice;
import com.spd.config.CommonConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class WebserviceConfigFactory {

	private static WebServiceConfig webserviceConfig;
	
	private WebserviceConfigFactory() {
		
	}
	
	public static WebServiceConfig getInstance() {
		if(webserviceConfig == null) {
			XStream xStream = new XStream(new DomDriver());
			//解析分三块内容，1.节点
			xStream.alias("Webservice", Webservice.class);
			xStream.alias("root", WebServiceConfig.class);
			//2.内容中的List
			xStream.addImplicitCollection(WebServiceConfig.class, "webserviceList");
			//3.属性信息
			xStream.aliasAttribute(Webservice.class, "name", "name");
			try {   
		        FileInputStream ops = new FileInputStream(new File(CommonConfig.WEBSERVICECONFIGPATH));   
		        webserviceConfig = (WebServiceConfig)xStream.fromXML(ops);   
		        ops.close();   
	        } catch (Exception e) {   
	        	LogTool.logger.error(CommonConfig.WEBSERVICECONFIGPATH + " 解析失败，详情:" + e.getMessage());
	        }  
		}
		return webserviceConfig;
	}
	
}
