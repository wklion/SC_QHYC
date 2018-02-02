package com.spd.common;

public enum StatisticsTypes {
	
	AVG("AVG"), MAX("MAX"), MIN("MIN"), SUM("SUM"), DAYS("DAYS");
	
	private String statisticsStr;
	
	public String getStatisticsStr() {
		return statisticsStr;
	}

	public void setStatisticsStr(String statisticsStr) {
		this.statisticsStr = statisticsStr;
	}

	private StatisticsTypes(String statisticsStr) {
		this.statisticsStr = statisticsStr;
	}
	
	public static StatisticsTypes getStatisticsTypeName(String name) {
        for (StatisticsTypes c : StatisticsTypes.values()) {
            if (c.getStatisticsStr().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
