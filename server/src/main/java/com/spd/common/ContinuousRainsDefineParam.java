package com.spd.common;

/**
 * 连阴雨，连续时间段查询的参数类。
 * @author Administrator
 *
 */
public class ContinuousRainsDefineParam {
	//轻度连阴雨，连续阴雨且无日照
	private int slightNoSSHDays;
	//轻度连阴雨，任意多少天雨量值
	private int slightPreDays;
	//轻度白天雨量界限值
	private double slightMinValue;
	//严重连阴雨，连续阴雨且无日照
	private int severityNoSSHDays;
	//严重连阴雨，任意多少天雨量值
	private int severityPreDays;
	//严重白天雨量界限值
	private double severityMinValue;
	//终止条件，连续多少天
	private int terminPreDays;
	//终止值
	private double terminValue;
	
	public int getSlightNoSSHDays() {
		return slightNoSSHDays;
	}
	public void setSlightNoSSHDays(int slightNoSSHDays) {
		this.slightNoSSHDays = slightNoSSHDays;
	}
	public int getSlightPreDays() {
		return slightPreDays;
	}
	public void setSlightPreDays(int slightPreDays) {
		this.slightPreDays = slightPreDays;
	}
	public double getSlightMinValue() {
		return slightMinValue;
	}
	public void setSlightMinValue(double slightMinValue) {
		this.slightMinValue = slightMinValue;
	}
	public int getSeverityNoSSHDays() {
		return severityNoSSHDays;
	}
	public void setSeverityNoSSHDays(int severityNoSSHDays) {
		this.severityNoSSHDays = severityNoSSHDays;
	}
	public int getSeverityPreDays() {
		return severityPreDays;
	}
	public void setSeverityPreDays(int severityPreDays) {
		this.severityPreDays = severityPreDays;
	}
	public double getSeverityMinValue() {
		return severityMinValue;
	}
	public void setSeverityMinValue(double severityMinValue) {
		this.severityMinValue = severityMinValue;
	}
	public int getTerminPreDays() {
		return terminPreDays;
	}
	public void setTerminPreDays(int terminPreDays) {
		this.terminPreDays = terminPreDays;
	}
	public double getTerminValue() {
		return terminValue;
	}
	public void setTerminValue(double terminValue) {
		this.terminValue = terminValue;
	}
	
}
