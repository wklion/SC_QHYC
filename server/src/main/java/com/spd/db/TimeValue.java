package com.spd.db;

import java.util.Date;

/**
 * 封装时间，和对应的值
 * @author Administrator
 *
 */
public class TimeValue {

	private Date date;
	
	private Double value;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
}
