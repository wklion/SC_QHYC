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
public final class ConnectionManager {

    private static ConnectionManager instance;
    //private static ComboPooledDataSource dataSource_SPPM; 	 //标准
    private Map connMap = new HashMap<String, ComboPooledDataSource>();
    
    private ConnectionManager()throws SQLException, PropertyVetoException  {      
    	//创建数据库链接
    	createConnn(CommonConfig.DBConn.DB_CONFIG_HOLDER);
    }
  
    


    
    public static synchronized final ConnectionManager getInstance() {
        if (instance == null) {
            try {
                instance = new ConnectionManager();
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
                dataSource.setUser(config.getUser());
                dataSource.setPassword(config.getPassword());
                //数据库连接串
                dataSource.setJdbcUrl(config.getIp());                
                dataSource.setDriverClass(config.getDriverClass());
                
//                dataSource.setInitialPoolSize(GAppUtil.DBParam.INI_POOL_SIZE);
//                dataSource.setMinPoolSize(10);
//                dataSource.setMaxPoolSize(100);
//                dataSource.setMaxStatements(50);
//                dataSource.setMaxIdleTime(60);
                dataSource.setMinPoolSize(config.getMinpoolsize());
                dataSource.setMaxPoolSize(config.getMaxpoolsize());
                dataSource.setMaxStatements(config.getMaxstatements());
                dataSource.setMaxIdleTime(config.getMaxidletime());
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