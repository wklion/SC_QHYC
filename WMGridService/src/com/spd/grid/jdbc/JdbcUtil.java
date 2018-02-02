package com.spd.grid.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.spd.grid.annotation.Entity;
import com.spd.grid.exception.NotFoundAnnotationException;


/**
 * 
 * @author wyp
 * @since JDK 1.6
 * @Date 2015-12-11
 */
public class JdbcUtil {
	
	

    public static String getTableName(Class<?> clazz) throws NotFoundAnnotationException {  
        if (clazz.isAnnotationPresent(Entity.class)) {  
            Entity entity = clazz.getAnnotation(Entity.class);  
            return entity.value();  
        } else {  
            throw new NotFoundAnnotationException(clazz.getName() + " is not Entity Annotation.");  
        }  
    }  
    
    /**
     * 设置SQL参数占位符的值 
     * @param values
     * @param ps
     * @param isSearch
     * @throws SQLException
     */
    public static void setParameter(List<Object> values, PreparedStatement ps, boolean isSearch)throws SQLException {  
    	
		for (int i = 1; i <= values.size(); i++) {
			Object fieldValue = values.get(i - 1);
			if(fieldValue==null){ // 如果为空 默认置  ""
				fieldValue="";
			}
			Class<?> clazzValue = fieldValue.getClass();
			if (clazzValue == String.class) {
				if (isSearch)
					ps.setString(i, "%" + (String) fieldValue + "%");
				else
					ps.setString(i, (String) fieldValue);

			} else if (clazzValue == boolean.class|| clazzValue == Boolean.class) {
				ps.setBoolean(i, (Boolean) fieldValue);
			} else if (clazzValue == byte.class || clazzValue == Byte.class) {
				ps.setByte(i, (Byte) fieldValue);
			} else if (clazzValue == char.class || clazzValue == Character.class) {
				ps.setObject(i, fieldValue, Types.CHAR);
			} else if (clazzValue == Date.class) {
				ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));
			} else if (clazzValue.isArray()) {
				Object[] arrayValue = (Object[]) fieldValue;
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < arrayValue.length; j++) {
					sb.append(arrayValue[j]).append("、");
				}
				ps.setString(i, sb.deleteCharAt(sb.length() - 1).toString());
			} else {
				ps.setObject(i, fieldValue, Types.NUMERIC);
			}
		}
	}
    
    
    
    /** 
     * 根据条件，返回sql条件和条件中占位符的值 
     * @param sqlWhereMap key：字段名 value：字段值 
     * @return 第一个元素为SQL条件，第二个元素为SQL条件中占位符的值 
     */ 
    public static List<Object> getSqlWhereWithValues(Map<String,Object> sqlWhereMap) {  
        if (sqlWhereMap.size() <1 ) return null;  
        List<Object> list = new ArrayList<Object>();  
        List<Object> fieldValues = new ArrayList<Object>();  
        StringBuffer sqlWhere = new StringBuffer(" where ");  
        Set<Entry<String, Object>> entrySets = sqlWhereMap.entrySet();  
        for (Iterator<Entry<String, Object>> iteraotr = entrySets.iterator();iteraotr.hasNext();) {  
            Entry<String, Object> entrySet = iteraotr.next();  
            fieldValues.add(entrySet.getValue());  
            Object value = entrySet.getValue();  
            if (value.getClass() == String.class) {  
                sqlWhere.append(entrySet.getKey()).append(" like ").append("?").append(" and ");  
            } else {  
                sqlWhere.append(entrySet.getKey()).append("=").append("?").append(" and ");  
            }  
        }  
        sqlWhere.delete(sqlWhere.lastIndexOf("and"), sqlWhere.length());  
        list.add(sqlWhere.toString());  
        list.add(fieldValues);  
        return list;  
    }  
    

    
    /** 
     * 释放数据库资源 
     */ 
    public static void release(PreparedStatement ps,ResultSet rs) {  
        try {  
         
            if (ps != null) {  
                ps.close();  
                ps = null;  
            }  
            if (rs != null) {  
                rs.close();  
                rs = null;  
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  

}
