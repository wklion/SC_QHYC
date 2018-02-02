package com.spd.grid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.spd.grid.tool.MathUtil;

/**
 * @作者:wangkun
 * @日期:2017年7月24日
 * @公司:spd
 * @说明:数据相碰操作
*/
public class DataUtil {
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:lsData-数据;calStart-开始日期;calEnd-结束日期;statistics-统计方式(0-平均;1-求和)
	 * @返回:
	 * @说明:月数据转日数据
	 */
	public List<Double> convertDayToMonth(List<Double> lsData,Calendar calStart,Calendar calEnd,int statistics){
		Boolean start = true;
		Calendar calS = (Calendar) calStart.clone();
		int curMonth = calS.get(Calendar.MONTH);//当前月
		//1、获取月时间结点
		double sum = 0;
		int count = 0;
		int index = 0;
		List<Double> lsResult = new ArrayList();//结果
		List<Integer> lsIndex = new ArrayList();//存放月结点
		while(calS.compareTo(calEnd)<1){
			int tempMonth = calS.get(Calendar.MONTH);//当前月
			if(tempMonth != curMonth){
				lsIndex.add(index);
				curMonth = tempMonth;
			}
			calS.add(Calendar.DATE, 1);
			index++;
		}
		lsIndex.add(index);
		
		//2、获取月数据
		MathUtil mathUtil = new MathUtil();
		int monthSize = lsIndex.size();
		for(int i=0;i<monthSize;i++){
			int curIndex = lsIndex.get(i);
			int preIndex = 0;
			if(i!=0){
				preIndex = lsIndex.get(i-1);
			}
			List<Double> item = lsData.subList(preIndex, curIndex);
			double val = 0;
			if(statistics==0){
				val = mathUtil.Avg(item);
			}
			else{
				val = mathUtil.Sum(item);
			}
			lsResult.add(val);
		}
		return lsResult;
	}
}
