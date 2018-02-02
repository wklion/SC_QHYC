package com.spd.common;
/**
 * 站类型
 * @author Administrator
 *
 */
public enum StationType {

	AWS("AWS"),
	MWS("MWS"),
	ALL("ALL"),
	AREA("AREA");//區域站
	private String type;
	
	public String getType() {
		return type;
	}
	private StationType(String type) {
		this.type = type;
	}
	public static StationType getStationType(String  type) {
		for (StationType c : StationType.values()) {
			if(c.getType().equals(type)) {
				return c;
			}
		}
		return null;
	}
}
