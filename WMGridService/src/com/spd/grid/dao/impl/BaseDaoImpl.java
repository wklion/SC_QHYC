package com.spd.grid.dao.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.grid.annotation.Column;
import com.spd.grid.annotation.Id;
import com.spd.grid.dao.BaseDao;
import com.spd.grid.exception.NotFoundAnnotationException;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.jdbc.JdbcUtil;
import com.spd.grid.tool.LogTool;



/**
 * BaseDao jdbc 实现
 * @author wyp
 *
 */
public class BaseDaoImpl<T> implements BaseDao<T> {
	 
	//表的别名  
    private static final String TABLE_ALIAS = "t";  
	
    @Override
	public void delete(Object id, Class<T> clazz) throws Exception {
    	 //获得表名  
        String tableName = JdbcUtil.getTableName(clazz);  
        //获得ID字段名和值  
        String idFieldName = "";  
        boolean flag = false;  
        Field[] fields = clazz.getDeclaredFields();  
        for (Field field : fields) {  
            if(field.isAnnotationPresent(Id.class)) {  
                idFieldName = field.getAnnotation(Id.class).value();  
                flag = true;  
                break;  
            }  
        }  
        if (!flag) {  
            throw new NotFoundAnnotationException(clazz.getName() + " object not found id property.");  
        }  
          
        //拼装sql  
        String sql = "delete from " + tableName + " where " + idFieldName + "=?";  
        Connection conn = DataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);  
        ps.setObject(1, id);  
        //执行SQL  
        ps.execute();  
        conn.close();
        JdbcUtil.release(ps,null);  
        LogTool.logger.info(sql + "\n" + clazz.getSimpleName() + "删除成功!");  
		
	}
    
	@Override
	public List<T> findAllByConditions(Map<String, Object> sqlWhereMap,Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<T>();  
        String tableName = JdbcUtil.getTableName(clazz);  
        String idFieldName = "";  
        //存储所有字段的信息  
        //通过反射获得要查询的字段  
        StringBuffer fieldNames = new StringBuffer();  
        Field[] fields = clazz.getDeclaredFields();  
        for (Field field : fields) {  
            String propertyName = field.getName();  
            if (field.isAnnotationPresent(Id.class)) {  
                idFieldName = field.getAnnotation(Id.class).value();  
                fieldNames.append(TABLE_ALIAS + "." + idFieldName).append(" as ").append(propertyName).append(",");  
            } else if (field.isAnnotationPresent(Column.class)) {  
                fieldNames.append(TABLE_ALIAS + "." + field.getAnnotation(Column.class).value()).append(" as ").append(propertyName).append(",");  
            }  
        }  
        fieldNames.deleteCharAt(fieldNames.length()-1);  
          
        //拼装SQL  
        String sql = "select " + fieldNames + " from " + tableName + " " + TABLE_ALIAS;  
        PreparedStatement ps = null;  
        List<Object> values = null;  
        if (sqlWhereMap != null) {  
            List<Object> sqlWhereWithValues = JdbcUtil.getSqlWhereWithValues(sqlWhereMap);  
            if (sqlWhereWithValues != null) {  
                //拼接SQL条件  
                String sqlWhere = (String)sqlWhereWithValues.get(0);  
                sql += sqlWhere;  
                //得到SQL条件中占位符的值  
                values = (List<Object>) sqlWhereWithValues.get(1);  
            }  
        }   
          
        //设置参数占位符的值  
        Connection conn = DataSource.getInstance().getConnection();
        
        if (values != null) {  
            ps = conn.prepareStatement(sql);  
            JdbcUtil.setParameter(values, ps, true);  
        } else {  
            ps = conn.prepareStatement(sql);  
        }  
          
          
        //执行SQL  
        ResultSet rs = ps.executeQuery();  
        while(rs.next()) {  
            T t = clazz.newInstance();  
           initObject(t, fields, rs);  
            list.add(t);  
        }  
          
        //释放资源  
        conn.close();
        JdbcUtil.release(ps, rs);  
       LogTool.logger.info(sql);  
        return list; 
	}

	@Override
	public T get(Object id, Class<T> clazz) throws Exception {
		 String idFieldName = "";  
	        Field[] fields = clazz.getDeclaredFields();  
	        boolean flag = false;  
	        for (Field field : fields) {  
	            if (field.isAnnotationPresent(Id.class)) {  
	                idFieldName = field.getAnnotation(Id.class).value();  
	                flag = true;  
	                break;  
	            }   
	        }  
	          
	        if (!flag) {  
	            throw new NotFoundAnnotationException(clazz.getName() + " object not found id property.");  
	        }  
	          
	        //拼装SQL  
	        Map<String,Object> sqlWhereMap = new HashMap<String, Object>();  
	        sqlWhereMap.put(TABLE_ALIAS + "." + idFieldName, id);  
	          
	        List<T> list = findAllByConditions(sqlWhereMap, clazz);  
	        return list.size() > 0 ? list.get(0) : null;  
	}

	@Override
	public void save(T t) throws Exception {
		 Class<?> clazz = t.getClass();  
	        //获得表名  
	        String tableName = JdbcUtil.getTableName(clazz);  
	        //获得字段  
	        StringBuilder fieldNames = new StringBuilder();     //字段名  
	        List<Object> fieldValues = new ArrayList<Object>(); //字段值  
	        StringBuilder placeholders = new StringBuilder();   //占位符  
	        Field[] fields = clazz.getDeclaredFields();  
	        for (Field field : fields) {  
	            PropertyDescriptor pd = new PropertyDescriptor(field.getName(),t.getClass());  
	            if (field.isAnnotationPresent(Id.class)) {  
	                fieldNames.append(field.getAnnotation(Id.class).value()).append(",");  
	                fieldValues.add(pd.getReadMethod().invoke(t));  
	            } else if(field.isAnnotationPresent(Column.class)) {  
	                fieldNames.append(field.getAnnotation(Column.class).value()).append(",");  
	                fieldValues.add(pd.getReadMethod().invoke(t));  
	            }  
	            placeholders.append("?").append(",");  
	        }  
	        //删除最后一个逗号  
	        fieldNames.deleteCharAt(fieldNames.length()-1);  
	        placeholders.deleteCharAt(placeholders.length()-1);  
	          
	        //拼接sql  
	        StringBuilder sql = new StringBuilder("");  
	        sql.append("insert into ").append(tableName).append(" (").append(fieldNames.toString()).append(") values (").append(placeholders).append(")") ;  
	        Connection conn = DataSource.getInstance().getConnection();
	        
	        PreparedStatement ps = conn.prepareStatement(sql.toString());  
	        //设置SQL参数占位符的值  
	        JdbcUtil.setParameter(fieldValues, ps, false);  
	        //执行SQL  
	        ps.execute();  
	        conn.close();
	        JdbcUtil.release(ps, null);  
	        LogTool.logger.info(sql + "\n" + clazz.getSimpleName() + "添加成功!");  
		
	}

	@Override
	public void update(T t) throws Exception {
		 Class<?> clazz = t.getClass();  
	        //获得表名  
	        String tableName = JdbcUtil.getTableName(clazz);  
	        //获得字段  
	        List<Object> fieldNames = new ArrayList<Object>();  //字段名  
	        List<Object> fieldValues = new ArrayList<Object>(); //字段值  
	        List<String> placeholders = new ArrayList<String>();//占位符  
	        String idFieldName = "";  
	        Object idFieldValue = "";  
	        Field[] fields = clazz.getDeclaredFields();  
	        for (Field field : fields) {  
	            PropertyDescriptor pd = new PropertyDescriptor(field.getName(),t.getClass());  
	            if (field.isAnnotationPresent(Id.class)) {  
	                idFieldName = field.getAnnotation(Id.class).value();  
	                idFieldValue = pd.getReadMethod().invoke(t);  
	            } else if(field.isAnnotationPresent(Column.class)) {  
	                fieldNames.add(field.getAnnotation(Column.class).value());  
	                fieldValues.add(pd.getReadMethod().invoke(t));  
	                placeholders.add("?");  
	            }  
	        }  
	        //ID作为更新条件，放在集合中的最后一个元素  
	        fieldNames.add(idFieldName);  
	        fieldValues.add(idFieldValue);  
	        placeholders.add("?");  
	          
	        //拼接sql  
	        StringBuilder sql = new StringBuilder("");  
	        sql.append("update ").append(tableName).append(" set ");  
	        int index = fieldNames.size() - 1;  
	        for (int i = 0; i < index; i++) {  
	            sql.append(fieldNames.get(i)).append("=").append(placeholders.get(i)).append(",");  
	        }  
	        sql.deleteCharAt(sql.length()-1).append(" where ").append(fieldNames.get(index)).append("=").append("?");  
	          
	        //设置SQL参数占位符的值  
	        Connection conn = DataSource.getInstance().getConnection();;
	        PreparedStatement ps = conn.prepareStatement(sql.toString());  
	        JdbcUtil.setParameter(fieldValues, ps, false);  
	          
	        //执行SQL  
	        ps.execute();  
	        conn.close();
	        JdbcUtil.release(ps, null);  
	        LogTool.logger.info(sql + "\n" + clazz.getSimpleName() + "修改成功.");  
		
	}
	
	
	
    /** 
     * 根据结果集初始化对象 
     */  
    private void initObject(T t, Field[] fields, ResultSet rs)  
            throws SQLException, IntrospectionException,  
            IllegalAccessException, java.lang.reflect.InvocationTargetException {  
        for (Field field : fields) {  
            String propertyName = field.getName();  
            Object paramVal = null;  
            Class<?> clazzField = field.getType();  
            if (clazzField == String.class) {  
                paramVal = rs.getString(propertyName);  
            } else if (clazzField == short.class || clazzField == Short.class) {  
                paramVal = rs.getShort(propertyName);  
            } else if (clazzField == int.class || clazzField == Integer.class) {  
                paramVal = rs.getInt(propertyName);  
            } else if (clazzField == long.class || clazzField == Long.class) {  
                paramVal = rs.getLong(propertyName);  
            } else if (clazzField == float.class || clazzField == Float.class) {  
                paramVal = rs.getFloat(propertyName);  
            } else if (clazzField == double.class || clazzField == Double.class) {  
                paramVal = rs.getDouble(propertyName);  
            } else if (clazzField == boolean.class || clazzField == Boolean.class) {  
                paramVal = rs.getBoolean(propertyName);  
            } else if (clazzField == byte.class || clazzField == Byte.class) {  
                paramVal = rs.getByte(propertyName);  
            } else if (clazzField == char.class || clazzField == Character.class) {  
                paramVal = rs.getCharacterStream(propertyName);  
            } else if (clazzField == Date.class) {  
                paramVal = rs.getTimestamp(propertyName);  
            } else if (clazzField.isArray()) {  
                paramVal = rs.getString(propertyName).split(",");   //以逗号分隔的字符串  
            }   
            PropertyDescriptor pd = new PropertyDescriptor(propertyName,t.getClass());  
            pd.getWriteMethod().invoke(t, paramVal);  
        }  
    }  
    

}
