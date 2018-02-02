package com.spd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.config.CommonConfig;
import com.spd.tool.ConnectionManager;
import com.spd.tool.LogTool;

public class BaseDao {

	//默认对应的数据库
	protected Connection getConn() {		
		return getConn(CommonConfig.DBConn.DB_SPMD);
	}
	
	//获取土壤的数据库连接
	protected Connection getConn(String connName) {		
		return ConnectionManager.getInstance().getConnection(connName);
	}
	
	protected boolean update(String sql) {
		boolean bReturn = false;
		
		Connection conn = null;
		int result ;
		
		try {
			conn = getConn();
			conn.setAutoCommit(false);
//			java.sql.Statement statement = conn.createStatement();
//			result = statement.executeUpdate(sql);
			PreparedStatement  pstmt = conn.prepareStatement(sql);
//			result = pstmt.executeUpdate();
			pstmt.executeUpdate(sql);
			conn.commit();
			bReturn  = true ;
			//LogTool.logger.info("")
		} catch (SQLException ex) {
			//LogTool.logger.error("批量更新出错，错误信息为"+ex.getMessage());
			LogTool.logger.error("更新出错，错误信息为" + ex.getMessage());
			try {
				conn.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}			
			
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bReturn;	
	}
	  /**
	    * 
	    * 功能:插入或者更新数据
	    * 作者: Ninglg
	    * 创建日期:2012-5-24
	    * @param sqlTemplate
	    * @param arrParam
	    */   
	   protected boolean insertOrUpdate(String sqlTemplate, ArrayList<Object[]> arrParam){	
			boolean bReturn = false;
		
			Connection conn = null;
			int[]result ;
			
			try {
				conn = getConn();
				conn.setAutoCommit(false);
				PreparedStatement  pstmt = conn.prepareStatement(sqlTemplate);
				if(arrParam != null) {
					for (Object[] objects : arrParam) {
					
						for (int i = 0; i < objects.length; i++) {
							pstmt.setObject(i+1, objects[i]);
						}										
						pstmt.addBatch();
					}
				}
				result = pstmt.executeBatch();
				conn.commit();
				bReturn  = true ;
				//LogTool.logger.info("")
			} catch (SQLException ex) {
				//LogTool.logger.error("批量更新出错，错误信息为"+ex.getMessage());
				LogTool.logger.error("批量更新出错，错误信息为" + ex.getMessage());
				try {
					conn.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}			
				
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return bReturn;		
		}
	   
	   
	    /**
	     * 
	     * 功能:
	     * 作者: Ninglg
	     * 创建日期:2012-6-29
	     * @param conn:传入的数据库连接
	     * @param sqlTemplate：sql模板语句
	     * @param params：要传入的参数
	     * @return ：ArrayList<Map<String, Object>>
	     */
		protected List query(Connection conn,String sqlTemplate,Object[] params ){
			List resultList = null;
			ResultSet result = null;
			int j = 1;
	        try {
	        	
	    		PreparedStatement stmt = conn.prepareStatement(sqlTemplate);
	    		if(params != null) {
		    		for (Object obj : params) {
		    			stmt.setObject(j++, obj);
					}
	    		}
	    		result = stmt.executeQuery();
				ResultSetMetaData meta = result.getMetaData();
				LinkedHashMap record = null;
				int count = meta.getColumnCount() + 1;
				resultList = new ArrayList();
								
				while (result.next()) {
					record = new LinkedHashMap();
					for (int i = 1; i < count; i++) {					
						int type = meta.getColumnType(i);
						Object obj;
						if(type == java.sql.Types.DATE || type == java.sql.Types.TIMESTAMP){
							obj =  result.getTimestamp(i);						
						}else{
							obj = result.getObject(i);
						}
						record.put(meta.getColumnLabel(i),obj );					
					}
					resultList.add(record);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				LogTool.logger.error("查询出错,"+e.getMessage());
	    
		    } finally {
		        try {
		            conn.close();
		        } catch (SQLException e) {		        	
		        	LogTool.logger.error("query", e);
		        }
		    }
		    return resultList;
		}
	
		/**
		 * 取到表结构
		 * @param conn
		 * @param sqlTemplate
		 * @return
		 */
	protected ResultSetMetaData getTableStruct(Connection conn,String sqlTemplate) {
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlTemplate);
			ResultSet result = stmt.executeQuery();
			ResultSetMetaData meta = result.getMetaData();
			return meta;
		} catch(Exception e) {
			LogTool.logger.error("查询出错,"+e.getMessage());
		} finally {
			try {
	            conn.close();
	        } catch (SQLException e) {		        	
	        	LogTool.logger.error("query", e);
	        }
		}
		return null;
	}
	/**
	 * 
	 * 功能:由sqlTemplate模板，构造sql语句。 作者: Ninglg 创建日期:2012-6-29
	 * 
	 * @param sqlTemplate
	 *            ：格式为INSERT INTO t_Soil_Hour_Data(%s) VALUES(%s)
	 * @param map
	 * @return
	 */
	private String constructorSql(String sqlTemplate, Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbKey = new StringBuilder();
		String sql = "";
		String[] keys = null;

		keys = map.keySet().toArray(new String[0]);

		for (int j = 0; j < keys.length; j++) {
			sb.append("?,");
			sbKey.append(keys[j] + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sbKey.deleteCharAt(sbKey.length() - 1);
		sql = String.format(sqlTemplate, sbKey.toString(), sb.toString());
		return sql;
	}
	
	/**
	 * 
	 * 功能:批量添加数据，为hashmap值，列名与值，构成的键值对
	 * 作者: Ninglg
	 * 创建日期:2012-6-29
	 * @param sqlTemplate:格式为INSERT INTO t_Soil_Hour_Data(%s) VALUES(%s)
	 * @param arrMapData :ArrayList<Map<String, Object>>
	 * @return
	 */
	protected boolean insertBatch(String sqlTemplate,
			List arrMapData) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String[] keys = null;
		String sql = "";
		int[] result;
		boolean bReturn = false;  
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			if (arrMapData.size() > 0) {
				Map<String, Object> map =(Map<String, Object>)arrMapData.get(0);
				sql = constructorSql(sqlTemplate, map);
				keys = map.keySet().toArray(new String[0]);
				pstmt = conn.prepareStatement(sql);
			}
			for (Map<String, Object> item : (ArrayList<Map<String, Object>>)arrMapData) {

				for (int j = 0; j < keys.length; j++) {
					String key = keys[j];
					pstmt.setObject(j + 1, item.get(key));
				}
				pstmt.addBatch();
			}
			if (arrMapData.size() > 0) {
				result = pstmt.executeBatch();
				conn.commit();
				bReturn = true;
			}	

		} catch (Exception e) {

			LogTool.logger.error(sql + "查询出错!" + e.getMessage());

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
		return bReturn ;
	}
	
	/**
	 * 针对m01d03这种类型
	 * @param sqlTemplate
	 * @param arrMapData
	 * @param resultSetMetaData
	 * @return
	 */
	protected boolean updateBatch(String sqlTemplate,
			List arrMapData, ResultSetMetaData resultSetMetaData) {
//		update t_pre_time_0808 set %s = %s where year = %s and Station_Id_C = %s
		Connection conn = null;
		PreparedStatement pstmt = null;
		String[] keys = null;
		String sql = "";
		int[] result;
		boolean bReturn = false;
		String columnName = "";
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			if (arrMapData.size() > 0) {
				if (arrMapData.size() > 0) {
					Map<String, Object> map =(Map<String, Object>)arrMapData.get(0);
					Set<String> keySet = map.keySet();
					Iterator<String> it = keySet.iterator();
					while(it.hasNext()) {
						String key = it.next();
						if(key.matches("m.*?d.*")) {
							sql = sqlTemplate.replace("%columns", key);
							columnName = key;
							break;
						}
					}
					pstmt = conn.prepareStatement(sql);
				}
			}
			for(int i=0; i<arrMapData.size(); i++) {
				Map<String, Object> item = (Map<String, Object>) arrMapData.get(i);
				pstmt.setObject(1, item.get(columnName));
				pstmt.setObject(2, item.get("id"));
				pstmt.addBatch();
				if((i != 0 && i % 300 == 0) || i == arrMapData.size() -1) {
					long s4 = System.currentTimeMillis();
					pstmt.executeBatch();
					pstmt.clearBatch();
					long s5 = System.currentTimeMillis();
					conn.commit();
					long s6 = System.currentTimeMillis();
//					System.out.println("花费时间 (s5 - s4)" + (s5 - s4) + ", (s6 -s5)" + (s6 -s5));
				}
			}
//			for (Map<String, Object> item : (ArrayList<Map<String, Object>>)arrMapData) {
//				pstmt.setObject(1, item.get(columnName));
//				pstmt.setObject(2, item.get("Station_Id_C"));
//				pstmt.addBatch();
//			}
//			if (arrMapData.size() > 0) {
//				conn.commit();
//				bReturn = true;
//			}	

		} catch (Exception e) {
			e.printStackTrace();
			LogTool.logger.error(sql + "查询出错!" + e.getMessage());
			LogTool.logger.error("参数值为:" + arrMapData.toString());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
//		System.out.println("end updateBatch");
		return bReturn ;
	}
	
	protected boolean updateBatch2(String sqlTemplate,
			List arrMapData, ResultSetMetaData resultSetMetaData, String[] columnName) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlTemplate);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		String sql = "";
		boolean bReturn = false;
		try {
//			conn = getConn();
			conn.setAutoCommit(false);
			for(int i=0; i<arrMapData.size(); i++) {
				Map<String, Object> item = (Map<String, Object>) arrMapData.get(i);
				for(int j = 0; j < columnName.length; j++) {
//					System.out.println("name:" + columnName[j] + " value:" + item.get(columnName[j]));
					pstmt.setObject(j + 1, item.get(columnName[j]));
				}
				pstmt.setObject(columnName.length + 1, item.get("id"));
				pstmt.addBatch();
				if((i != 0 && i % 300 == 0) || i == arrMapData.size() -1) {
					pstmt.executeBatch();
					pstmt.clearBatch();
					conn.commit();
				}
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			LogTool.logger.error(sql + "查询出错!" + e.getMessage());
			LogTool.logger.error("参数值为:" + arrMapData.toString());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
		return bReturn ;
	}
	
	protected boolean updateBatch3(String sqlTemplate,
			List arrMapData, ResultSetMetaData resultSetMetaData, String[] columnName) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlTemplate);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		String sql = "";
		boolean bReturn = false;
		try {
//			conn = getConn();
			conn.setAutoCommit(false);
			for(int i=0; i<arrMapData.size(); i++) {
				Map<String, Object> item = (Map<String, Object>) arrMapData.get(i);
				for(int j = 0; j < columnName.length; j++) {
					pstmt.setObject(j + 1, item.get(columnName[j]));
				}
				pstmt.addBatch();
				if((i != 0 && i % 300 == 0) || i == arrMapData.size() -1) {
					pstmt.executeBatch();
					pstmt.clearBatch();
					conn.commit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogTool.logger.error(sql + "查询出错!" + e.getMessage());
			LogTool.logger.error("参数值为:" + arrMapData.toString());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
		return bReturn ;
	}
	/**
	 * 
	 * 功能:批量添加数据，为hashmap值，列名与值，构成的键值对，添加对类型的控制
	 * 作者: xianchao
	 * 创建日期:2012-6-29
	 * @param sqlTemplate:格式为INSERT INTO t_Soil_Hour_Data(%s) VALUES(%s)
	 * @param arrMapData :ArrayList<Map<String, Object>>
	 * @return
	 */
	protected boolean insertBatch(String sqlTemplate,
			List arrMapData, ResultSetMetaData resultSetMetaData) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		String[] keys = null;
		String sql = "";
		int[] result;
		boolean bReturn = false;  
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			if (arrMapData.size() > 0) {
				Map<String, Object> map =(Map<String, Object>)arrMapData.get(0);
				sql = constructorSql(sqlTemplate, map);
				keys = map.keySet().toArray(new String[0]);
				pstmt = conn.prepareStatement(sql);
			}
			for (Map<String, Object> item : (ArrayList<Map<String, Object>>)arrMapData) {

				for (int j = 0; j < keys.length; j++) {
					String key = keys[j];
					int columnCount = resultSetMetaData.getColumnCount();
					for(int i=0; i<columnCount; i++) {
						String columnName = resultSetMetaData.getColumnName(i+1);
						if(columnName.equalsIgnoreCase(key)) {
							String type = resultSetMetaData.getColumnTypeName(i+1);
							if(type.equals("DATE")) {
								String dateStr = (String)item.get(key);
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								java.util.Date date = sdf.parse(dateStr);
								long time = date.getTime();
								Timestamp timestamp = new Timestamp(time);
								pstmt.setTimestamp(j + 1, timestamp);
							} else {
								pstmt.setObject(j + 1, item.get(key));//, tempType);
							}
							break;
						}
					}
					
				}
				pstmt.addBatch();
			}
			if (arrMapData.size() > 0) {
				result = pstmt.executeBatch();
				conn.commit();
				bReturn = true;
			}	

		} catch (Exception e) {
			e.printStackTrace();
			LogTool.logger.error(sql + "查询出错!" + e.getMessage());
//			LogTool.logger.error("参数值为:" + arrMapData.toString());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
		return bReturn ;
	}
	
	protected String createUpdateSQL(String updateSQL, Map<String, Object> map) {
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String column = it.next();
			if(column.equals("id")) continue;
			updateSQL += " " + column + " = ?,";
		}
		updateSQL = updateSQL.substring(0, updateSQL.length() - 1);
		updateSQL += " where id = ?"; 
		return updateSQL;
	}
	
	/**
	 * 
	 * 功能:批量添加数据，为hashmap值，列名与值，构成的键值对，添加对类型的控制
	 * 作者: xianchao
	 * 创建日期:2012-6-29
	 * @param sqlTemplate:格式为INSERT INTO t_Soil_Hour_Data(%s) VALUES(%s)
	 * @param arrMapData :ArrayList<Map<String, Object>>
	 * @return
	 */
	protected boolean insertBatch2(String sqlTemplate,
			List arrMapData, ResultSetMetaData resultSetMetaData) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		String[] keys = null;
		String sql = "";
		int[] result;
		boolean bReturn = false;  
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			if (arrMapData.size() > 0) {
				Map<String, Object> map =(Map<String, Object>)arrMapData.get(0);
				sql = sqlTemplate;
				keys = map.keySet().toArray(new String[0]);
				pstmt = conn.prepareStatement(sql);
			}
			for (Map<String, Object> item : (ArrayList<Map<String, Object>>)arrMapData) {

				for (int j = 0; j < keys.length; j++) {
					String key = keys[j];
					int columnCount = resultSetMetaData.getColumnCount();
					for(int i=0; i<columnCount; i++) {
						String columnName = resultSetMetaData.getColumnName(i+1);
						if(columnName.equalsIgnoreCase(key)) {
							String type = resultSetMetaData.getColumnTypeName(i+1);
							if(type.equals("DATE")) {
								String dateStr = (String)item.get(key);
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								java.util.Date date = sdf.parse(dateStr);
								long time = date.getTime();
								Timestamp timestamp = new Timestamp(time);
								pstmt.setTimestamp(j + 1, timestamp);
							} else {
								pstmt.setObject(j + 1, item.get(key));//, tempType);
							}
							break;
						}
					}
					
				}
				pstmt.addBatch();
			}
			if (arrMapData.size() > 0) {
				result = pstmt.executeBatch();
				conn.commit();
				bReturn = true;
			}	

		} catch (Exception e) {

			LogTool.logger.error(sql + "查询出错!" + e.getMessage());
			LogTool.logger.error("参数值为:" + arrMapData.toString());

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error("query", e);
			}
		}
		return bReturn ;
	}
	
	/**
	 * 
	 * 功能:把查询的结果转换为 set 对象
	 * 作者: Ninglg
	 * 创建日期:2012-6-29
	 * @param conn：查询的对象
	 * @param sqlTemplate：sql语句模板
	 * @param params：要传入的参数
	 * @return
	 */
	protected Set queryToSet(Connection conn,String sqlTemplate,Object[] params ){
		ResultSet result = null;
		PreparedStatement stmt = null;
		
		Set setRet = null;
		int i = 1;
		try {
			setRet = new HashSet<String>();
	
			stmt = conn.prepareStatement(sqlTemplate);
			if(params != null) {
				for (Object obj : params) {
					stmt.setObject(i++, obj);
				}					
			}
			result = stmt.executeQuery();
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (result.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				for(int j=0; j<columnCount; j++) {
					String metaDataName = metaData.getColumnName(j+1);
					String value = result.getString(j+1);
					map.put(metaDataName, value);
				}
				setRet.add(map);
			}
		
		} catch (Exception e) {
			LogTool.logger.error("fetchRemoteSoilData",e);
		
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error(e.getMessage());
			}
		}  
		return setRet;		
	}
	
	/**
	 * 把结果存放到Set中，Set只有结果，没有字段名
	 * @param conn
	 * @param sqlTemplate
	 * @param params
	 * @return
	 */
	protected Set queryResultToSet(Connection conn,String sqlTemplate,Object[] params ){
		ResultSet result = null;
		PreparedStatement stmt = null;
		
		Set setRet = null;
		int i = 1;
		try {
			setRet = new HashSet<String>();
	
			stmt = conn.prepareStatement(sqlTemplate);
			if(params != null) {
				for (Object obj : params) {
					stmt.setObject(i++, obj);
				}					
			}
			result = stmt.executeQuery();
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (result.next()) {
//				HashMap<String, String> map = new HashMap<String, String>();
				for(int j=0; j<columnCount; j++) {
					String metaDataName = metaData.getColumnName(j+1);
					String value = result.getString(j+1);
					setRet.add(value);
//					map.put(metaDataName, value);
				}
//				setRet.add(map);
			}
		
		} catch (Exception e) {
			LogTool.logger.error("fetchRemoteSoilData",e);
		
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LogTool.logger.error(e.getMessage());
			}
		}  
		return setRet;		
	}
	
}
