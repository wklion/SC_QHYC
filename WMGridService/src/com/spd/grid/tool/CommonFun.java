package com.spd.grid.tool;

import java.util.List;
import com.mg.objects.Workspace;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月1日
 * @MODIFY:2016年11月1日
 * @DESCRIPTION:通用方法
 */
public class CommonFun {
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月1日
	 * @RETURN:平均值
	 * @PARAM:data-数组
	 * @DESCRIPTION:计算数组平均值
	 */
	public static double CalAvg(double[] data)
	{
		double sum=0;
		int size=data.length;
		if(size==0)
			return 0;
		for(int i=0;i<size;i++)
		{
			sum+=data[i];
		}
		return sum/size;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月3日
	 * @RETURN:平均值
	 * @PARAM:data-数组
	 * @DESCRIPTION:计算数组平均值
	 */
	public static double CalAvg(List<Double> data)
	{
		double sum=0;
		int size=data.size();
		if(size==0)
			return 0;
		for(int i=0;i<size;i++)
		{
			sum+=data.get(i);
		}
		double result=sum/size;
		result=(int)(result*100)/100.0;
		return result;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月1日
	 * @RETURN:
	 * @PARAM:ws-工作空间
	 * @DESCRIPTION:关闭工作空间下的所有数据源
	 */
	public static void CloseDS(Workspace ws)
	{
		if(ws==null)
			return;
		int dsCount=ws.GetDatasourceCount();
		for(int i=dsCount;i>0;i--)
		{
			String dsName=ws.GetDatasource(i-1).GetAlias();
			ws.CloseDatasource(dsName);
		}
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月21日
	 * @修改日期:2018年1月21日
	 * @参数:
	 * @返回:
	 * @说明:数组包含
	 */
	public Boolean ArrayIsContain(String[] strs,String str){
		Boolean result = false;
		int size = strs.length;
		for(int i=0;i<size;i++){
			if(strs[i].equals(str)){
				result = true;
				break;
			}
		}
		return result;
	}
}
