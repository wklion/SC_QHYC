package com.spd.tool;

import java.io.FileInputStream;
import java.util.*;

import com.spd.config.CommonConfig;
import com.spd.pojo.DBConfig;
import com.spd.pojo.RecordConfig;

public class PropertiesUtil {
	
	       
    public  static void loadSysCofing() {    	    	
    	loadDBConnConfig();
    	loadDBType();
    	loadMCIConfig();
    	loadACQConfig();
	}  
       
    public static void loadCIMISSConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			String cimissURL = prop.getProperty("cimiss.url");
			CommonConfig.CIMISSURL = cimissURL;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadMCIConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			String mciPath = prop.getProperty("mcipath");
			CommonConfig.MCIPATH = mciPath;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadACQConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			String acqPath = prop.getProperty("acqpath");
			CommonConfig.ACQPATH = acqPath;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadFTPConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.FTPPATH);
			prop.load(fis);           //加载属性文   件
			String rootPath = prop.getProperty("RootPath");
			String itemDirName = prop.getProperty("ItemDirName");
			CommonConfig.FTPROOTDIR = rootPath;
			CommonConfig.FTPITEMDIRNAME = itemDirName;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadSHPConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			String cimissURL = prop.getProperty("shppath");
			CommonConfig.SHPPATH = cimissURL;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadMODISConfig() {
    	try {
    		Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.MODISPATH);
			prop.load(fis);           //加载属性文   件
			String ndviSavePath = prop.getProperty("ndvi.savepath");
			CommonConfig.MODISNDVI_SAVEPATH = ndviSavePath;
			fis.close();
		} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());
		}
    }
    
    public static void loadDBType() {
    	try {
	    	Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			String type = prop.getProperty("DBType");
			CommonConfig.DBConn.dbType = type;
			fis.close();
    	} catch (Exception e) {
			LogTool.logger.error("iniDBConfig,"+e.getMessage());		
		}
    }
    /**
     * 
     * 功能:初始化数据库配置信息
     * 作者: Ninglg
     * 创建日期:2012-6-28
     */
    private static void loadDBConnConfig(){
    	try {
	    	Properties prop = new Properties();
			FileInputStream fis =   new FileInputStream(CommonConfig.DBCONFIGPATH);
			prop.load(fis);           //加载属性文   件
			iniDBParam(prop);			
			fis.close();
    	} catch (Exception e) {
    		LogTool.logger.error("iniDBConfig,"+e.getMessage());		
		}
    }
        
    
    /*
     * 初始化数据参数信息。
     */
    private static void iniDBParam(Properties prop){    
    	               
       String [] arrConnName = CommonConfig.DBConn.getDBNames();
       DBConfig config = null;
       String key ="";
       List arrDBConfig = new ArrayList<DBConfig>();
       for (String connName : arrConnName) {
    	   config = new DBConfig(connName);
    	   //
    	  /* key = connName + GAppUtil.DBConn.POSTFIX_DIALECT;
    	   if (prop.contains(key)) {
    		   config.setdriact(prop.getProperty(key));
    	   }*/
//    	   key = connName + CommonConfig.DBConn.POSTFIX_DRIVERCLASS;
//    	   if (prop.containsKey(key)) {
//    		   config.setDriverClass(prop.getProperty(key));
//    	   }
    	   
    	   key = connName + CommonConfig.DBConn.POSTFIX_IP;
    	   if (prop.containsKey(key)) {
    		   config.setIp(prop.getProperty(key));
    	   }
    	   key = connName + CommonConfig.DBConn.POSTFIX_PORT;
    	   if (prop.containsKey(key)) {
    		   config.setPort(Integer.parseInt(prop.getProperty(key)));
    	   }
    	   key = connName + CommonConfig.DBConn.POSTFIX_USER;
    	   if (prop.containsKey(key)) {
    		   config.setUser(prop.getProperty(key));
    	   }
    	   
    	   key = connName + CommonConfig.DBConn.POSTFIX_PASSWORD;
    	   if (prop.containsKey(key)) {
    		   config.setPassword(prop.getProperty(key));
    	   }    
    	   key = connName + CommonConfig.DBConn.POSTFIX_DB;
    	   if (prop.containsKey(key)) {
    		   config.setDb(prop.getProperty(key));
    	   }  
    	   key = connName + CommonConfig.DBConn.POSTFIX_DRIVER;
    	   if (prop.containsKey(key)) {
    		   config.setDriverClass(prop.getProperty(key));
    	   }  
    	   key = connName + CommonConfig.DBConn.POSTFIX_MAXIDLETIME;
    	   if (prop.containsKey(key)) {
    		   config.setMaxidletime(Integer.parseInt(prop.getProperty(key)));
    	   }
    	   key = connName + CommonConfig.DBConn.POSTFIX_MAXPOOLSIZE;
    	   if (prop.containsKey(key)) {
    		   config.setMaxpoolsize(Integer.parseInt(prop.getProperty(key)));
    	   }  
    	   key = connName + CommonConfig.DBConn.POSTFIX_MAXSTATEMENTS;
    	   if (prop.containsKey(key)) {
    		   config.setMaxstatements(Integer.parseInt(prop.getProperty(key)));
    	   }  
    	   key = connName + CommonConfig.DBConn.POSTFIX_MINPOOLSIZE;
    	   if (prop.containsKey(key)) {
    		   config.setMinpoolsize(Integer.parseInt(prop.getProperty(key)));
    	   }  
    	   
    	   key = connName + CommonConfig.DBConn.RECORD_RECORDSTARTTIME;
    	   if (prop.containsKey(key)) {
    		   RecordConfig.recordStartTime = prop.getProperty(key);
    	   }  
    	   
    	   key = connName + CommonConfig.DBConn.RECORD_RECORDENDTIME;
    	   if (prop.containsKey(key)) {
    		   RecordConfig.recordEndTime = prop.getProperty(key);
    	   }  
    	   
    	   arrDBConfig.add(config);
       }    	
       CommonConfig.DBConn.DB_CONFIG_HOLDER = arrDBConfig;
    }
    
  
    public static void main(String[] args) {
		String str = "<a href=\"javascript:dayDetailsShow(&quot;81411600&quot;,&quot;2015-11-06 06:00:00&quot;,&quot;2015-11-06 14:00:00&quot;,&quot;B10001450722&quot;);void(0);\">旺盛江水库</a>\"";
		System.out.println(str);
		str = str.replaceAll("<a href=\"javascript:dayDetailsShow\\(&quot;81411600&quot;,&quot;", "");
		str = str.replaceAll("&quot;,&quot;", "");
		str = str.replaceAll("&quot;\\);void\\(0\\);\">", "");
		str = str.replaceAll("</a>", "");
		System.out.println(str);
	}
}
