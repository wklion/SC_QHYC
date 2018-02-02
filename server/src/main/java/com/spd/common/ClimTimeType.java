package com.spd.common;
/**
 * 气候上的时间划分
 * @author Administrator
 *
 */
public enum ClimTimeType {

	DAY("DAY"), // 日
	FIVEDAYS("FIVEDAYS"), // 候
	TENDAYS("TENDAYS"), //旬
	MONTH("MONTH"), //月
	SEASON("SEASON"), //季
	YEAR("YEAR"); //年
	
	private String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	private ClimTimeType(String typeName) {
		this.typeName = typeName;
	}
	
	public static ClimTimeType getClimTimeType(String name) {
        for (ClimTimeType c : ClimTimeType.values()) {
            if (c.getTypeName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
