package com.spd.pojo;

import java.util.List;

public class ResultDesc {

	//表格显示的字段名
	private String tableShowItems;
	//地图显示的字段名
	private String gisShowItems;
	//中英文对应 例子：Station_Id_C=站号,Station_Name=站名
	private List itemDescription;
	
	public String getTableShowItems() {
		return tableShowItems;
	}
	public void setTableShowItems(String tableShowItems) {
		this.tableShowItems = tableShowItems;
	}
	public String getGisShowItems() {
		return gisShowItems;
	}
	public void setGisShowItems(String gisShowItems) {
		this.gisShowItems = gisShowItems;
	}
	public List getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(List itemDescription) {
		this.itemDescription = itemDescription;
	}
	
}
