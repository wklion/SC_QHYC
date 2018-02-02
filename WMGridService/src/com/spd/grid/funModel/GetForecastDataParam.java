package com.spd.grid.funModel;

public class GetForecastDataParam {
	private String elementID;
	private String[] methods;
	private String makeDate;
	private String[] forecastDates;
    public String getElementID() {
        return elementID;
    }
    public void setElementID(String elementID) {
        this.elementID = elementID;
    }
    public String[] getMethods() {
        return methods;
    }
    public void setMethods(String[] methods) {
        this.methods = methods;
    }
    public String getMakeDate() {
        return makeDate;
    }
    public void setMakeDate(String makeDate) {
        this.makeDate = makeDate;
    }
    public String[] getForecastDates() {
        return forecastDates;
    }
    public void setForecastDates(String[] forecastDates) {
        this.forecastDates = forecastDates;
    }
	
}
