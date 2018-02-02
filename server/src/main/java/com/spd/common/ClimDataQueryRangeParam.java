package com.spd.common;
/**
 * 资料信息检索中，按时间段查询的参数
 * @author Administrator
 *
 */
public class ClimDataQueryRangeParam {

	private TimesParam timesParam;
	
	private String tableName;
	
	private String orderType;

	public TimesParam getTimesParam() {
		return timesParam;
	}

	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	
}
