package com.spd.model;

public class Task {
	private String id;
	private String name;
	private String parantDic;
	private String format;
	private int timeSpace;
	private String timeFormat;
	private String outputDic;
	public String getOutputDic() {
		return outputDic;
	}
	public void setOutputDic(String outputDic) {
		this.outputDic = outputDic;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParantDic() {
		return parantDic;
	}
	public void setParantDic(String parantDic) {
		this.parantDic = parantDic;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getTimeSpace() {
		return timeSpace;
	}
	public void setTimeSpace(int timeSpace) {
		this.timeSpace = timeSpace;
	}
	public String getTimeFormat() {
		return timeFormat;
	}
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
}
