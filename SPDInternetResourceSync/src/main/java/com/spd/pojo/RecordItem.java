package com.spd.pojo;
/**
 * 补录的单项配置
 * @author Administrator
 *
 */
public class RecordItem {
	//接口
	private String method;
	//开始时间 yyyyMMddHHmmss
	private String startTime;
	//结束时间
	private String endTime;
	// 额外的参数
	private String args;
	
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
