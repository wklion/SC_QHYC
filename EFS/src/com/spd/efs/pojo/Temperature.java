package com.spd.efs.pojo;

import java.math.BigDecimal;

public class Temperature {
    public String stationNum;
    public String stationName;
    public double tem_avg;
    public double tem_anomaly;
    
    
    
    
    
	public String getStationNum() {
		return stationNum;
	}
	public void setStationNum(String stationNum) {
		this.stationNum = stationNum;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public double getTem_anomaly() {
		return tem_anomaly;
	}
	public void setTem_anomaly(double temAnomaly) {
		tem_anomaly = temAnomaly;
	}
	public double getTem_avg() {
		return tem_avg;
	}
	public void setTem_avg(double temAvg) {
		tem_avg = temAvg;
	}

    
    
    
    
}
