package com.spd.grid.domain;

public class Depart {
	private int departID;
	private int areaID;
	private String departName;
	private int parentID;
	private String departCode;
	private String codeOfTownForecast;
	private String codeOfGuidanceForecast;
	
	public Depart(int departID,int areaID,String departName, int parentID, String DepartCode, String CodeOfTownForecast, String CodeOfGuidanceForecast)
	{
		this.departID = departID;
		this.areaID = areaID;
		this.departName = departName;		
		this.parentID = parentID;
		this.departCode = DepartCode;
		this.codeOfTownForecast = CodeOfTownForecast;
		this.codeOfGuidanceForecast = CodeOfGuidanceForecast;
	}
	
	public int getDepartID()
	{
		return this.departID;
	}
	public void setDepartID(Integer value)
	{
		this.departID = value;
	}
	
	public int getAreaID()
	{
		return this.areaID;
	}
	public void setAreaID(int value)
	{
		this.areaID = value;
	}
	
	public String getDepartName()
	{
		return this.departName;
	}
	public void setDepartName(String value)
	{
		this.departName = value;
	}
	
	public int getParentID()
	{
		return this.parentID;
	}
	public void setParentID(int value)
	{
		this.parentID = value;
	}
	
	public String getDepartCode()
	{
		return this.departCode;
	}
	public void setDepartCode(String value)
	{
		this.departCode = value;
	}
	
	public String getCodeOfTownForecast()
	{
		return this.codeOfTownForecast;
	}
	public void setCodeOfTownForecast(String value)
	{
		this.codeOfTownForecast = value;
	}
	
	public String getCodeOfGuidanceForecast()
	{
		return this.codeOfGuidanceForecast;
	}
	public void setCodeOfGuidanceForecast(String value)
	{
		this.codeOfGuidanceForecast = value;
	}
}
