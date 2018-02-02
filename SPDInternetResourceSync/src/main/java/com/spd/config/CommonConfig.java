package com.spd.config;

import java.util.List;

public class CommonConfig {
	//ResourceConfig.xml所在路径
	
	public static String CIMISSURL;
	//MCI文件路径
	public static String MCIPATH;
	//ACQ预报报文路径
	public static String ACQPATH;

	public static String FTPROOTDIR;
	
	public static String FTPITEMDIRNAME;
	//shp文件所在位置
	public static String SHPPATH;

	public static String MODISNDVI_SAVEPATH;
	
	public static final String RESOURCECONFIGPATH = "config/ResourceConfig.xml";

	public static final String MAPPINGCONFIGPATH = "config/AddressMapping.xml";

	public static final String DBCONFIGPATH = "config/dbconfig.properties";
															
	public static final String TABTIMEDATACONFIGPATH = "config/TabTimeDataConfig.xml";

	public static final String SEACONDITIONCONFIGPATH = "config/SeaCondition.xml";

	public static final String SHPCONFIGPATH = "config/Ship.xml";

	public static final String WEBSERVICECONFIGPATH = "config/WebServiceConfig.xml";
	//同步用户上传FTP文件路径
	public static final String FTPPATH = "config/ftp.properties";

	public static final String MODISPATH = "config/modis.properties";
	
	public static class DBConn{
		//MongoDB库
		public static final String DB_SPPM ="sppm";
		//关系型数据库
		public static final String DB_SPMD ="spmd";
		//数据库类型
		public static String dbType = "";
		
		public static String[] getDBNames(){
			String [] arr ={DB_SPPM, DB_SPMD};
			return arr;
		}		
		//public static final String  POSTFIX_DIALECT= ".conn.dialect";
		public static final String  POSTFIX_IP= ".ip";
		public static final String	POSTFIX_USER= ".user";
		public static final String  POSTFIX_PASSWORD=".password";
		public static final String	POSTFIX_PORT =".port";
		public static final String	POSTFIX_DB =".db";
		public static final String	POSTFIX_DRIVER =".driverclass";
		public static final String	POSTFIX_MINPOOLSIZE =".minpoolsize";
		public static final String	POSTFIX_MAXPOOLSIZE =".maxpoolsize";
		public static final String	POSTFIX_MAXSTATEMENTS =".maxstatements";
		public static final String	POSTFIX_MAXIDLETIME =".maxidletime";
		public static final String	RECORD_RECORDSTARTTIME =".recordstarttime";
		public static final String	RECORD_RECORDENDTIME =".recordendtime";
		//数据库配置信息容器
		public static List DB_CONFIG_HOLDER = null;
		
	}
}
