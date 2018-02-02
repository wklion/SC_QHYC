package com.spd.tool;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.spd.config.CommonConfig;
import com.spd.pojo.DBConfig;

/**
 * 
 * 创建日期:2012-6-28
 * Title:文件所属模块
 * Description：连接管理文件

 * @author 宁利广
 * @mender：（文件的修改者，文件创建者之外的人）
 * @version 1.0
 * Remark：认为有必要的其他信息
 */
public final class MSConnectionManager {

    private static MSConnectionManager instance;
    //private static ComboPooledDataSource dataSource_SPPM; 	 //标准
    private Map connMap = new HashMap<String, ComboPooledDataSource>();
    
    private MSConnectionManager()throws SQLException, PropertyVetoException  {      
    	//创建数据库链接
    	createConnn(CommonConfig.DBConn.DB_CONFIG_HOLDER);
    }
  
    


    
    public static synchronized final MSConnectionManager getInstance() {
        if (instance == null) {
            try {
                instance = new MSConnectionManager();
            } catch (Exception e) {
            	LogTool.logger.error("getInstance", e);
            }
        }
        return instance;
    }
    
    
    /**
     * 
     * 功能: createConnn
     * 作者: Ninglg
     * 创建日期:2012-6-28
     * @param arrDB
     */    
    public void createConnn(List<DBConfig> arrDB){
    	ComboPooledDataSource dataSource = null;
    	try {
    		for (DBConfig config : arrDB) {
        		
        		dataSource = new ComboPooledDataSource();
        		//用户
                dataSource.setUser("Clim");
                dataSource.setPassword("123");
                //数据库连接串
                dataSource.setJdbcUrl("jdbc:sqlserver://172.24.186.74;DatabaseName=ClimData;user=Clim;password=123");                
                dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setMinPoolSize(5);
                dataSource.setMaxPoolSize(20);
                dataSource.setMaxStatements(50);
                dataSource.setMaxIdleTime(60);
                connMap.put(config.getConnName(), dataSource);                
    		}
		} catch (Exception e) {
			LogTool.logger.error("创建数据库连接失败，"+e.getMessage());
		}
    	
    }

    //获取标准库数据库连接
    public synchronized final Connection getConnection(String connName) {
        Connection conn = null;
        try {
        	if (connMap.containsKey(connName)) {
        		conn = ((ComboPooledDataSource)connMap.get(connName)).getConnection();
			}else {
				throw new Exception("未找到【"+connName+"】,对应的数据库连接！");
			}
            
        } catch (Exception e) {
        	LogTool.logger.error("getConnection", e);
        }
        return conn;
    }
        
    
}