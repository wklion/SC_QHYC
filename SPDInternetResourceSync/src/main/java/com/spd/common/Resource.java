package com.spd.common;

public class Resource {
	
	private int id;
	
	private String name;
	
	private int type;
	
	private String urlAddress;
	
	private String urlAddressRegex;
	
	private String subAddress;
	
	private String subAddressRegex;
	
	private String imageRegex;
	
	private String saveDir;
	
	private boolean UTC;
	
	private String rename;
	
	private String timeIndex;
	
	private String fileType;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getTimeIndex() {
		return timeIndex;
	}

	public void setTimeIndex(String timeIndex) {
		this.timeIndex = timeIndex;
	}

	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}

	public boolean isUTC() {
		return UTC;
	}

	public void setUTC(boolean uTC) {
		UTC = uTC;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrlAddress() {
		return urlAddress;
	}

	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}

	public String getUrlAddressRegex() {
		return urlAddressRegex;
	}

	public void setUrlAddressRegex(String urlAddressRegex) {
		this.urlAddressRegex = urlAddressRegex;
	}

	public String getSubAddress() {
		return subAddress;
	}

	public void setSubAddress(String subAddress) {
		this.subAddress = subAddress;
	}

	public String getSubAddressRegex() {
		return subAddressRegex;
	}

	public void setSubAddressRegex(String subAddressRegex) {
		this.subAddressRegex = subAddressRegex;
	}

	public String getImageRegex() {
		return imageRegex;
	}

	public void setImageRegex(String imageRegex) {
		this.imageRegex = imageRegex;
	}

}
