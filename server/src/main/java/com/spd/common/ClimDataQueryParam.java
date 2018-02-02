package com.spd.common;
/**
 * 资料信息检索中，按时间查询各种气象要素的参数类
 * @author Administrator
 *
 */
public class ClimDataQueryParam {
	//查询的要素
	private String elements;
	//排序方式
	private String orderType;
	//时间点
	private String time;
	
	public String getElements() {
		return elements;
	}
	public void setElements(String elements) {
		this.elements = elements;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
