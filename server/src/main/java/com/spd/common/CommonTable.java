package com.spd.common;

import java.util.HashMap;

/**
 * 定义基础类型表对应的字段类型
 * @author Administrator
 *
 */
public class CommonTable {

	private static HashMap<String, String> tableType = new HashMap<String, String>();
	
	private static CommonTable commonTable ;
	
	private CommonTable() {
		
	}
	
	public static CommonTable getInstance() {
		if(commonTable == null) {
			commonTable = new CommonTable();
			tableType.put("t_tem_avg", "BigDecimal");
		}
		return commonTable;
	}
	
	public static String getTypeByTableName(String tableName) {
		String type = tableType.get(tableName);
		return type;
	}
}
