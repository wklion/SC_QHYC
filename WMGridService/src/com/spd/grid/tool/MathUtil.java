package com.spd.grid.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月10日
 * @DESCRIPTION:数学相关
 */
public class MathUtil {
	public double[] CalFactor(List<Double> lsObs,List<List<Double>> lsData) throws Exception
	{
		int factorSize=lsData.size();//因子个数
		int factorLength=lsData.get(0).size();//因子长度
		int obsLength=lsObs.size();
		StringBuilder sbObsData=new StringBuilder();
		for(int i=0;i<obsLength;i++)
		{
			Double val=lsObs.get(i);
			sbObsData.append(val+",");
		}
		int index=sbObsData.lastIndexOf(",");
		sbObsData=sbObsData.deleteCharAt(index);
		StringBuilder sbData=new StringBuilder();
		for(int i=0;i<factorSize;i++)
		{
			List<Double> lsSeries=lsData.get(i);
			for(int j=0;j<factorLength;j++)
			{
				Double val=lsSeries.get(j);
				sbData.append(val+",");
			}
		}
		index=sbData.lastIndexOf(",");
		sbData=sbData.deleteCharAt(index);
		
		Runtime rn = Runtime.getRuntime();
		Process p = null;
		String file=ComfigureUtil.config.getOsrFile();
		File fi=new File(file);
		if(!fi.exists()){
			LogTool.logger.error(file+"不存在!");
			return null;
		}
		try 
		{
			String param=String.format("%s %s %s %s %s", file,factorSize,factorLength,sbData,sbObsData);
			p = rn.exec(param);
			p.waitFor();
		} 
		catch (Exception e) {
		}
		BufferedReader info = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		StringBuilder infoMsg = new StringBuilder();
		String line = null;
		double[] result=new double[factorSize+1];//第一个为常量
		while ((line = info.readLine()) != null) {
				infoMsg.append(line).append(",");
        }
		index=infoMsg.lastIndexOf(",");
		if(index==-1){
			System.out.println("数据处理错误!");
			return null;
		}
		infoMsg=infoMsg.deleteCharAt(index);
		String[] strVals=infoMsg.toString().split(",");
		int valLength=strVals.length;
		if(valLength!=(factorSize+1))
		{
			System.out.println("接收到的参数错误");
			return null;
		}
		for(int i=0;i<valLength;i++)
		{
			result[i]=Double.parseDouble(strVals[i]);
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月15日
	 * @修改日期:2016年12月15日
	 * @参数:data-矩阵
	 * @返回:
	 * @说明:求逆矩阵
	 */
	public double[][] GetNiMatrix(double[][] data){
		double A = GetHL(data);// 先是求出行列式的模|data|
		int size=data.length;
		double[][] newData = new double[size][size];// 创建一个等容量的逆矩阵 
		for (int i = 0; i < data.length; i++) {  
            for (int j = 0; j < data.length; j++) {  
            	double num;  
                if ((i + j) % 2 == 0) {  
                    num = GetHL(GetDY(data, i + 1, j + 1));  
                } else {  
                    num = -GetHL(GetDY(data, i + 1, j + 1));  
                }
                newData[i][j] = num / A;  
            }  
        }
		newData = getA_T(newData);
		return newData;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月15日
	 * @修改日期:2016年12月15日
	 * @参数:data-矩阵
	 * @返回:
	 * @说明:求矩阵的模
	 */
	private double GetHL(double[][] data){
		double result=0;
		if(data.length==2){
			return data[0][0] * data[1][1] - data[0][1] * data[1][0];
		}
		int size=data.length;
		double[] SpreadVal=new double[size];
		for(int i=0;i<size;i++){
			if(i%2==0){
				SpreadVal[i] = data[0][i] * GetHL(GetDY(data, 1, i + 1));
			}
			else{
				SpreadVal[i] = -data[0][i] * GetHL(GetDY(data, 1, i + 1));
			}
		}
		 for (int i = 0; i < size; i++) {  
			 result += SpreadVal[i];  
	     } 
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月15日
	 * @修改日期:2016年12月15日
	 * @参数:data-矩阵，h-行，v-列
	 * @返回:代数余子式(矩阵)
	 * @说明:求解代数余子式 输入：原始矩阵+行+列 现实中真正的行和列数目
	 */
	private double[][] GetDY(double[][] data,int h,int v){
		int H = data.length;  
        int V = data[0].length;  
        double[][] newData = new double[H - 1][V - 1];
        for (int i = 0; i < newData.length; i++) {  
            if (i < h - 1) {  
                for (int j = 0; j < newData[i].length; j++) {  
                    if (j < v - 1) {  
                        newData[i][j] = data[i][j];  
                    } else {  
                        newData[i][j] = data[i][j + 1];  
                    }  
                }  
            } else {  
                for (int j = 0; j < newData[i].length; j++) {  
                    if (j < v - 1) {  
                        newData[i][j] = data[i + 1][j];  
                    } else {  
                        newData[i][j] = data[i + 1][j + 1];  
                    }  
                }  
  
            }  
        } 
        return newData;  
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月15日
	 * @修改日期:2016年12月15日
	 * @参数:A-矩阵
	 * @返回:转置矩阵
	 * @说明:矩阵转置
	 */
	public double[][] getA_T(double[][] A) {  
        int h = A.length;  
        int v = A[0].length;  
        // 创建和A行和列相反的转置矩阵  
        double[][] A_T = new double[v][h];  
        // 根据A取得转置矩阵A_T  
        for (int i = 0; i < h; i++) {  
            for (int j = 0; j < v; j++) {  
                A_T[j][i] = A[i][j];  
            }  
        }  
        return A_T;  
    }
	/**
	 * @作者:wangkun
	 * @日期:2016年12月15日
	 * @修改日期:2016年12月15日
	 * @参数:A-矩阵，b-矩阵
	 * @返回:矩阵
	 * @说明:矩阵乘法
	 */
	public double[][] MulMatrix(double[][] a,double[][] b){
		int aSize=a.length;
		int bSize=b.length;
		int b0Size=b[0].length;
		double result[][] = new double[aSize][b0Size];
		int x,i,j;
		for(i=0;i<aSize;i++){
			for(j=0;j<b0Size;j++){
				double temp=0;
				for(x = 0;x<bSize;x++)  
                {  
                    temp+=a[i][x]*b[x][j];
                } 
				result[i][j] = temp;
			}
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月31日
	 * @修改日期:2016年12月31日
	 * @参数:data-原数据
	 * @返回:平均数
	 * @说明:计算平均数
	 */
	public double Avg(List<Double> data){
		int size=data.size();
		if(size==0){
			return 0;
		}
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum+=data.get(i);
		}
		return sum/size;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年09月20日
	 * @修改日期:2017年09月20日
	 * @参数:data-原数据
	 * @返回:平均数
	 * @说明:计算平均数
	 */
	public double Avg(double[] data){
		int size=data.length;
		if(size==0){
			return 0;
		}
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum+=data[i];
		}
		return sum/size;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:
	 * @返回:
	 * @说明:求和
	 */
	public double Sum(List<Double> data){
		int size=data.size();
		if(size==0){
			return 0;
		}
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum+=data.get(i);
		}
		return sum;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:
	 * @返回:
	 * @说明:求和
	 */
	public double Sum(double[] data){
		int size=data.length;
		if(size==0){
			return 0;
		}
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum+=data[i];
		}
		return sum;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月31日
	 * @修改日期:2016年12月31日
	 * @参数:data-原数据
	 * @返回:方差
	 * @说明:计算方差
	 */
	public double FangCha(List<Double> data){
		int size=data.size();
		if(size==0){
			System.out.println("不能计算方差");
			return 0;
		}
		double avg=Avg(data);//计算平均数
		double sum2=0;
		for(int i=0;i<size;i++){
			double val=data.get(i);
			sum2+=(val-avg)*(val-avg);
		}
		double s2=sum2/size;
		return s2;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月9日
	 * @修改日期:2017年1月9日
	 * @参数:
	 * @返回:
	 * @说明:标准化
	 */
	public double[][] BiaoZhuiHua(double[][] data){
		int nSize=data.length;//空间点
		int tSize=data[0].length;//观测时序
		double[][] result = new double[nSize][tSize];
		for (int n = 0; n < nSize; n++){
			double sum = 0;
			for (int t = 0; t < tSize; t++)
            {
                sum += data[n][t];
            }
			double avg = sum / tSize;
			sum=0;
            for (int t = 0; t < tSize; t++)
            {
            	sum +=(data[n][t]-avg)*(data[n][t]-avg);
            }
            double bzc=Math.sqrt(sum/tSize);
            for(int t=0;t<tSize;t++){
            	result[n][t]=(data[n][t]-avg)/bzc;
            }
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月15日
	 * @修改日期:2017年1月15日
	 * @参数:lsdata-原数据
	 * @返回:标准差
	 * @说明:简单标准差
	 */
	public List<Double> BZC(List<Double> lsData){
		double avg=Avg(lsData);
		int size=lsData.size();
		List<Double> lsResult=new ArrayList<>();
		for(int i=0;i<size;i++){
			double val=lsData.get(i)-avg;
			lsResult.add(val);
		}
		return lsResult;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月9日
	 * @修改日期:2017年1月9日
	 * @参数:data-需处理数据
	 * @返回:标准化的数据
	 * @说明:标准化处理
	 */
	public double[][] StandDeal(double[][] data){
			int tSize=data.length;//时间
			int nSize=data[0].length;//空间
			double[][] result = new double[tSize][nSize];//结果
			for(int n=0;n<nSize;n++){
					double sum = 0;
					for (int t = 0; t < tSize; t++){
		                	sum += data[t][n];
		            }
					double avg = sum / tSize;
					sum=0;
		            for (int t = 0; t < tSize; t++){
		            		sum +=(data[t][n]-avg)*(data[t][n]-avg);
		            }
		            double bzc=Math.sqrt(sum/tSize);
		            for(int t=0;t<tSize;t++){
		            		result[t][n]=(data[t][n]-avg)/bzc;
		            }
			}
			return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月25日
	 * @修改日期:2017年7月25日
	 * @参数:
	 * @返回:
	 * @说明:最大值
	 */
	public double Max(List<Double> lsData){
		double max = lsData.get(0);
		for(double item:lsData){
			if(item>max){
				max = item;
			}
		}
		return max;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月25日
	 * @修改日期:2017年7月25日
	 * @参数:
	 * @返回:
	 * @说明:最小值
	 */
	public double Min(List<Double> lsData){
		double min = lsData.get(0);
		for(double item:lsData){
			if(item<min){
				min = item;
			}
		}
		return min;
	}
}
