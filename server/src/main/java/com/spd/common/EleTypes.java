package com.spd.common;

public enum EleTypes {
	
	AVGTEM("t_tem_avg", "AVGTEM"), //平均气温
	AVGTEMMAX("t_tem_max", "AVGTEMMAX"), //最高气温
	AVGTEMMIN("t_tem_min", "AVGTEMMIN"), //最低气温
	PRETIME0808("t_pre_time_0808", "PRETIME0808"), //08-08降水
	PRETIME0820("t_pre_time_0820", "PRETIME0820"), //08-20降水
	PRETIME2008("t_pre_time_2008", "PRETIME2008"), //20-08降水
	PRETIME2020("t_pre_time_2020", "PRETIME2020"), //20-20降水
	RAINSTORM0808("t_rainstorm0808", "RAINSTORM0808"), //08-08降水
	RAINSTORM0820("t_rainstorm0820", "RAINSTORM0820"), //08-20降水
	RAINSTORM2008("t_rainstorm2008", "RAINSTORM2008"), //20-08降水
	RAINSTORM2020("t_rainstorm2020", "RAINSTORM2020"), //20-20降水
	RHUAVG("t_rhu_avg", "RHUAVG"), //相对湿度
	WINS2MIAVG("t_win_s_2mi_avg", "WINS2MIAVG"), //平均风速
	PRSAVG("t_prs_avg", "PRSAVG"), //平均气压
	SSH("t_ssh", "SSH"), //日照对数
	VISMIN("t_vis_min", "VISMIN"), //能见度
	TEMGAP("v_tem_gap", "TEMGAP"); //温度日较差

	private String tableName;

	private String name;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private EleTypes(String tableName, String name) {
		this.tableName = tableName;
		this.name = name;
	}
	
	public static String getTableName(String name) {
        for (EleTypes c : EleTypes.values()) {
            if (c.getName().equals(name)) {
                return c.tableName;
            }
        }
        return null;
    }
	
	public static String getName(String tableName) {
        for (EleTypes c : EleTypes.values()) {
            if (c.getTableName().equals(tableName)) {
                return c.name;
            }
        }
        return null;
    }
	
};