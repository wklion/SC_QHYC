package com.spd.qhyc.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
	/**
	 * @作者:wangkun
	 * @日期:2017年11月16日
	 * @修改日期:2017年11月16日
	 * @参数:rs-查询记录,clazz-实体类
	 * @返回:
	 * @说明:记录转实体类
	 */
	public List  populate(ResultSet rs , Class clazz){
		List list = new ArrayList();
		if(rs==null){
			return null;
		}
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			Field[] fields = clazz.getDeclaredFields();
			while(rs.next()){
				Object obj = clazz.newInstance();//构造业务对象实体
				for(int i = 1;i<=colCount;i++){//将每一个字段取出进行赋值
					Object value = rs.getObject(i);
					for(int j=0;j<fields.length;j++){//寻找该列对应的对象属性
						Field f = fields[j];
						if(f.getName().equalsIgnoreCase(rsmd.getColumnName(i))){
							boolean flag = f.isAccessible();
							f.setAccessible(true);
				                         f.set(obj, value);
				                         f.setAccessible(flag);
						}
					}
				}
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
