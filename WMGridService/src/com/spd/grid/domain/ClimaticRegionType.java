package com.spd.grid.domain;

/*
 * 
 * 气候区划类型
 * */
public class ClimaticRegionType {
	private String datasetname;
	private String typename;
	
	public ClimaticRegionType(String datasetname,String typename)
	{
		this.datasetname = datasetname;
		this.typename = typename;
	}
	
	public String getDatasetName()
	{
		return this.datasetname;
	}
	
	public void setDatasetName(String value)
	{
		this.datasetname = value;
	}
	
	public String getTypeName()
	{
		return this.typename;
	}
	
	public void setTypeName(String value)
	{
		this.typename = value;
	}
}
