package com.spd.common;
/**
 * 县镇名、县镇Code、区县名、区县Code 的实体，
 * @author xianchao
 *
 */
public class AddressLocation {
	
	private String countyName;

	private String countyCode;
	
	private String townName;
	
	private String townCode;

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	public String getTownCode() {
		return townCode;
	}

	public void setTownCode(String townCode) {
		this.townCode = townCode;
	}
	
	
}
