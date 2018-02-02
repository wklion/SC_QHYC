package com.spd.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.spd.tool.CommonTool;

/**
 * 气候操作
 * @author Administrator
 *
 */
public class ClimTime {

	//日期字符串
	private String climTimeStr;
	//开始时间
	private Date startDate;
	//开始时间字符串
	private String startStr;
	//结束时间
	private Date endDate;
	//结束时间字符串
	private String endStr;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 根据开始，结束时间，取到对应的气候
	 * @param startDate
	 * @param endDate
	 * @param climTimeType
	 * @return
	 */
	public static List<ClimTime> getClimTimeByTimes(Date startDate, Date endDate, ClimTimeType climTimeType) {
		List<ClimTime> resultList = new ArrayList<ClimTime>();
		switch(climTimeType) {
			case DAY:
				for(long time = startDate.getTime(); time <= endDate.getTime(); time += CommonConstant.DAYTIMES) {
					ClimTime climTime = new ClimTime();
					Date itemStartDate = new Date(time);
					climTime.setStartDate(itemStartDate);
					climTime.setStartStr(sdf.format(itemStartDate));
					Date itemEndDate = new Date(time);
					climTime.setEndDate(itemEndDate);
					String timeStr = sdf.format(itemEndDate);
					climTime.setEndStr(timeStr);
					climTime.setClimTimeStr(timeStr.substring(0, 4) + "年" + timeStr.substring(5, 7) + "月" + timeStr.substring(8, 10) + "日");
					resultList.add(climTime);
				}
				break;
			case FIVEDAYS:
				LinkedHashSet set = new LinkedHashSet();
				for(long start = startDate.getTime(); start <= endDate.getTime(); start += CommonConstant.DAYTIMES) {
					int[] hous = CommonTool.getHouTimesByDate(new Date(start));
					String str = hous[0] + "_" + hous[1] + "_" + hous[2];
					set.add(str);
				}
				Iterator it = set.iterator();
				while(it.hasNext()) {
					String str = (String) it.next();
					String[] temp = str.split("_");
					int[] hous = new int[3];
					hous[0] = Integer.parseInt(temp[0]);
					hous[1] = Integer.parseInt(temp[1]);
					hous[2] = Integer.parseInt(temp[2]);
					Date[] dates = CommonTool.getDateRangeByHou(hous[0], hous[1], hous[2]);
					Date tempStartDate = null, tempEndDate = null;
					if(dates[0].getTime() < startDate.getTime()) {
						tempStartDate = startDate;
					} else {
						tempStartDate = dates[0];
					}
					if(dates[1].getTime() > endDate.getTime()) {
						tempEndDate = endDate;
					} else {
						tempEndDate = dates[1];
					}
					ClimTime climTime = new ClimTime();
					climTime.setStartDate(tempStartDate);
					climTime.setEndDate(tempEndDate);
					climTime.setStartStr(sdf.format(tempStartDate));
					climTime.setEndStr(sdf.format(tempEndDate));
					climTime.setClimTimeStr(hous[0] + "年" + hous[1] + "月" + hous[2] + "候");
					resultList.add(climTime);
				}
				break;
			case TENDAYS:
				LinkedHashSet tendaysSet = new LinkedHashSet();
				for(long i = startDate.getTime(); i <= endDate.getTime(); i+= CommonConstant.DAYTIMES) {
					String result = CommonTool.getYearMonTenDaysFromDate(new Date(i));
					tendaysSet.add(result);
				}
				Iterator tendaysIt = tendaysSet.iterator();
				while(tendaysIt.hasNext()) {
					String tendaystr = (String) tendaysIt.next();
					Date[] dates = CommonTool.getDateFromTenDaysStr(tendaystr);
					Date tempStartDate = null, tempEndDate = null;
					if(startDate.getTime() > dates[0].getTime()) {
						tempStartDate = startDate;
					} else {
						tempStartDate = dates[0];
					}
					if(endDate.getTime() < dates[1].getTime()) {
						tempEndDate = endDate;
					} else {
						tempEndDate = dates[1];
					}
					ClimTime climTime = new ClimTime();
					climTime.setStartDate(tempStartDate);
					climTime.setEndDate(tempEndDate);
					climTime.setStartStr(sdf.format(tempStartDate));
					climTime.setEndStr(sdf.format(tempEndDate));
					int tenday = Integer.parseInt(tendaystr.substring(6, 8));
					String tendayResult = "";
					if(tenday == 1) {
						tendayResult = "上旬";
					} else if(tenday == 2) {
						tendayResult = "中旬";
					} else if (tenday == 3) {
						tendayResult = "下旬";
					}
					climTime.setClimTimeStr(tendaystr.substring(0, 4) + "年" + tendaystr.substring(4, 6) + "月" + tendayResult);
					resultList.add(climTime);
				}
				break;
			case MONTH:
				SimpleDateFormat yyyymmSDF = new SimpleDateFormat("yyyyMM");
				int start = Integer.parseInt(yyyymmSDF.format(startDate));
				int end = Integer.parseInt(yyyymmSDF.format(endDate));
				for(int i = start; i <= end; i++) {
					ClimTime climTime = new ClimTime();
					Date tempStartDate = null, tempEndDate = null;
					Date itemStartDate = null;
					try {
						itemStartDate = yyyymmSDF.parse(i + "");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Date itemEndDate = null;
					climTime.setClimTimeStr((i / 100) + "年" + (i % 100) + "月");
					if(i % 100 == 12) {
						i += 100;
						i -= 11; // 把201512 变成201601
						try {
							itemEndDate = yyyymmSDF.parse(i + "");
							itemEndDate = new Date(itemEndDate.getTime() - CommonConstant.DAYTIMES);
						} catch (ParseException e) {
							e.printStackTrace();
						}
//						i--;
					} else {
						try {
							itemEndDate = yyyymmSDF.parse((i + 1) + "");
							itemEndDate = new Date(itemEndDate.getTime() - CommonConstant.DAYTIMES);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if(itemStartDate.getTime() < startDate.getTime()) {
						tempStartDate = startDate;
					} else {
						tempStartDate = itemStartDate;
					}
					if(itemEndDate.getTime() > endDate.getTime()) {
						tempEndDate = endDate;
					} else {
						tempEndDate = itemEndDate;
					}
					climTime.setStartDate(tempStartDate);
					climTime.setEndDate(tempEndDate);
					climTime.setStartStr(sdf.format(tempStartDate));
					climTime.setEndStr(sdf.format(tempEndDate));
//					climTime.setClimTimeStr((i / 100) + "年" + (i % 100) + "月");
					resultList.add(climTime);
				}
				break;
			case SEASON:
				List<String> resultStrs = CommonTool.getSeasonByDates(startDate, endDate);
				for(String resultStr : resultStrs) {
					ClimTime climTime = new ClimTime();
					climTime.setStartStr(resultStr);
					Date[] tempDate = CommonTool.getSeasonByDates(startDate, endDate, resultStr);
					climTime.setStartDate(tempDate[0]);
					climTime.setEndDate(tempDate[1]);
					climTime.setStartStr(sdf.format(tempDate[0]));
					climTime.setEndStr(sdf.format(tempDate[1]));
					climTime.setClimTimeStr(resultStr);
					resultList.add(climTime);
				}
				break;
			case YEAR:
				SimpleDateFormat yearSDF = new SimpleDateFormat("yyyy");
				int startYear = Integer.parseInt(yearSDF.format(startDate));
				int endYear = Integer.parseInt(yearSDF.format(endDate));
				for(int i = startYear; i <= endYear; i++) {
					ClimTime climTime = new ClimTime();
					climTime.setClimTimeStr(i + "年");
					if(i == startYear) {
						climTime.setStartDate(startDate);
						climTime.setStartStr(sdf.format(startDate));
					} else {
						String startStr = i + "-01-01 00:00:00";
						try {
							climTime.setStartDate(sdf.parse(startStr));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						climTime.setStartStr(startStr);
					}
					if (i == endYear) {
						climTime.setEndDate(endDate);
						climTime.setEndStr(sdf.format(endDate));
					} else {
						String endStr = i + "-12-31 00:00:00";
						try {
							climTime.setEndDate(sdf.parse(endStr));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						climTime.setEndStr(endStr);
					}
					resultList.add(climTime);
				}
				break;
			default:
				break;
				
		}
		return resultList;
	}

	private void setClimTimeStr(String climTimeStr) {
		this.climTimeStr = climTimeStr;
	}

	private void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	private void setStartStr(String startStr) {
		this.startStr = startStr;
	}

	private void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public String getClimTimeStr() {
		return climTimeStr;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStartStr() {
		return startStr;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getEndStr() {
		return endStr;
	}
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String startStr = "20150503", endStr = "20150604";
		Date startDate = sdf.parse(startStr);
		Date endDate = sdf.parse(endStr);
		List<ClimTime> result = ClimTime.getClimTimeByTimes(startDate, endDate, ClimTimeType.DAY);
		for(int i = 0; i < result.size(); i++) {
			ClimTime climTime = result.get(i);
			System.out.println(climTime.getClimTimeStr());
		}
	}
}
