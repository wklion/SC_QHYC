package com.spd.common;

public class MetadictData {

	private int id;
	
	private String URL;
	
	private String storePath;
	
	private String interfaceAddress;
	
	private String username;
	
	private String password;
	
	private String fileTimeStr;
	
	private boolean isUTC;
	
	private String productCode;
	
	private String level1name;
	
	private String level2name;
	
	private String level3name;

	private String productName;

	public String getLevel1name() {
		return level1name;
	}

	public void setLevel1name(String level1name) {
		this.level1name = level1name;
	}

	public String getLevel2name() {
		return level2name;
	}

	public void setLevel2name(String level2name) {
		this.level2name = level2name;
	}

	public String getLevel3name() {
		return level3name;
	}

	public void setLevel3name(String level3name) {
		this.level3name = level3name;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getInterfaceAddress() {
		return interfaceAddress;
	}

	public void setInterfaceAddress(String interfaceAddress) {
		this.interfaceAddress = interfaceAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFileTimeStr() {
		return fileTimeStr;
	}

	public void setFileTimeStr(String fileTimeStr) {
		this.fileTimeStr = fileTimeStr;
	}

	public boolean isUTC() {
		return isUTC;
	}

	public void setUTC(boolean isUTC) {
		this.isUTC = isUTC;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	
}
