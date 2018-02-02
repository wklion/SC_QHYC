package com.spd.grid.domain;

/*
 * 
 * 气候区划子项
 * */
public class ClimaticRegionItem {
	private String regionname;
	private Integer regionid;
	
	public ClimaticRegionItem(String regionname,Integer regionid)
	{
		this.regionname = regionname;
		this.regionid = regionid;
	}
	
	public String getRegionName()
	{
		return this.regionname;
	}
	
	public void setRegionName(String value)
	{
		this.regionname = value;
	}
	
	public Integer getRegionId()
	{
		return this.regionid;
	}
	
	public void setRegionId(Integer value)
	{
		this.regionid = value;
	}
}
