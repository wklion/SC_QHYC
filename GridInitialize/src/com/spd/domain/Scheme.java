package com.spd.domain;

public class Scheme {
	public String type;
	public String makeTime;
	public Integer startHour;
	public Integer endHour;
	public Integer forecastHour;
	public String element;
	public String model;
	public String modelMakeTime;
	public Integer modelForecastHour;
	public String hourspan;
	public double defaultDataValue;
	public int valid;
	
	public Scheme(String type,String makeTime,Integer startHour,Integer endHour,Integer forecastHour,String element,
			String model,String modelMakeTime,Integer modelForecastHour,String hourspan,double defaultDataValue, int valid)
	{
		this.type = type;
		this.makeTime = makeTime;
		this.startHour = startHour;
		this.endHour = endHour;
		this.forecastHour = forecastHour;
		this.element = element;
		this.model = model;
		this.modelMakeTime = modelMakeTime;
		this.modelForecastHour = modelForecastHour;
		this.hourspan = hourspan;
		this.defaultDataValue = defaultDataValue;
		this.valid = valid;
	}
}
