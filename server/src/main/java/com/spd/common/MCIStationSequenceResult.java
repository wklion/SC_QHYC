package com.spd.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * MCI站次统计结果
 * @author Administrator
 *
 */
public class MCIStationSequenceResult implements Comparator<MCIStationSequenceResult> {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//时间
	private String datetime;
	//特旱
	private int level4;
	//重旱
	private int level3;
	//中旱
	private int level2;
	//轻旱
	private int level1;
	//有旱
	private int existDays;
	//无旱
	private int noDays;
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public int getLevel4() {
		return level4;
	}
	public void setLevel4(int level4) {
		this.level4 = level4;
	}
	public int getLevel3() {
		return level3;
	}
	public void setLevel3(int level3) {
		this.level3 = level3;
	}
	public int getLevel2() {
		return level2;
	}
	public void setLevel2(int level2) {
		this.level2 = level2;
	}
	public int getLevel1() {
		return level1;
	}
	public void setLevel1(int level1) {
		this.level1 = level1;
	}
	public int getExistDays() {
		return existDays;
	}
	public void setExistDays(int existDays) {
		this.existDays = existDays;
	}
	public int getNoDays() {
		return noDays;
	}
	public void setNoDays(int noDays) {
		this.noDays = noDays;
	}
	public int compare(MCIStationSequenceResult o1, MCIStationSequenceResult o2) {
		String datetime1 = o1.getDatetime();
		String datetime2 = o2.getDatetime();
		try {
			long time1 = sdf.parse(datetime1).getTime();
			long time2 = sdf.parse(datetime2).getTime();
			if(time1 < time2) return -1;
			if(time1 == time2) return 0;
			if(time1 > time2) return 1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
