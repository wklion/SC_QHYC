package com.spd.common;

public enum FilterTypes {
	// >=, >, <, <=, between
	GET("GET"),
	GT("GT"),
	LT("LT"),
	LET("LET"),
	BETWEEN("BETWEEN"),
	EQUALS("EQUALS");

	private String typeName;
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	private FilterTypes(String typeName) {
		this.typeName = typeName;
	}
	
	public static FilterTypes getFilterTypeName(String name) {
        for (FilterTypes c : FilterTypes.values()) {
            if (c.getTypeName().equals(name)) {
                return c;
            }
        }
        return null;
    }
};
