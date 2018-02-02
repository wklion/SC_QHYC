package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.spd.common.Group;
import com.spd.common.Resource;
import com.spd.common.ResourceItem;
import com.spd.common.Resources;
import com.spd.config.CommonConfig;
import com.spd.dao.impl.MetadictDao;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ResourceFactory {

	private static Resources resources;
	
	private static String resourceConfigMD5 = "";
	
	private ResourceFactory() {
		
	}
	
	public static synchronized List<ResourceItem> getInstanceFromDB() {
		MetadictDao metadictDao = new MetadictDao();
		return metadictDao.getInternetMeta();
	}
	
	public static synchronized List<ResourceItem> getKeYanSuoFromDB() {
		MetadictDao metadictDao = new MetadictDao();
		return metadictDao.getKeYanSuoSMBInternetMeta();
	}
	
	public static synchronized Resources getInstance() {
		try {
			MD5Tool md5Tool = new MD5Tool();
			String resourceConfigMD52 = md5Tool.getFileMD5String(new File(CommonConfig.RESOURCECONFIGPATH));
			if(!resourceConfigMD5.equals(resourceConfigMD52)) {
				resourceConfigMD5 = resourceConfigMD52;
				XStream xStream = new XStream(new DomDriver());
				//解析分三块内容，1.节点
				xStream.alias("Resources", Resources.class);
				xStream.alias("Resource", Resource.class);
				xStream.alias("Group", Group.class);
				//2.内容中的List
				xStream.addImplicitCollection(Resources.class, "groups");
				xStream.addImplicitCollection(Group.class, "resources");
				//3. 属性信息
				xStream.aliasAttribute(Group.class, "id", "id");
				xStream.aliasAttribute(Group.class, "groupName", "groupName");
				xStream.aliasAttribute(Resource.class, "id", "id");
				xStream.aliasAttribute(Resource.class, "type", "type");
				xStream.aliasAttribute(Resource.class, "name", "name");
				try {   
			        FileInputStream ops = new FileInputStream(new File(CommonConfig.RESOURCECONFIGPATH));   
			        resources = (Resources)xStream.fromXML(ops);   
			        ops.close();   
		        } catch (Exception e) {   
		        	LogTool.logger.error(CommonConfig.RESOURCECONFIGPATH + " 解析失败，详情:" + e.getMessage());
		        }  
		        System.out.println(resources);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return resources;
	}
	
	public static void main(String[] args) {
		ResourceFactory.getInstance();
	}
	
}
