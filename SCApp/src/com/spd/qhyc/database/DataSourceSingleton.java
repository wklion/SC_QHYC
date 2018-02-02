package com.spd.qhyc.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.spd.qhyc.config.BaseDataBaseConfig;
import com.spd.qhyc.model.BaseDataBase;

public class DataSourceSingleton {
	
	
	    private DataSourceSingleton() {}  
	 
	    private static DruidDataSource dataSource=null;  
	    
	    
	    
	    public static DruidDataSource getInstance() {  
	         if (dataSource == null) {
	        	 BaseDataBaseConfig baseDataBaseConfig = new BaseDataBaseConfig();
	        	 BaseDataBase baseDataBase = baseDataBaseConfig.get();
	        	 dataSource = new DruidDataSource();
	        	 dataSource.setDriverClassName("com.mysql.jdbc.Driver"); 
	        	 dataSource.setUsername(baseDataBase.getUser());
	        	 dataSource.setPassword(baseDataBase.getPassword());
	        	 dataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", baseDataBase.getServer(), baseDataBase.getPort(), baseDataBase.getDatabase()));
	        	 dataSource.setInitialSize(5);
	        	 dataSource.setMinIdle(1);
	        	 dataSource.setMaxActive(10); // 启用监控统计功能  dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);
	         }    
	        return dataSource;  
	    }  

}
