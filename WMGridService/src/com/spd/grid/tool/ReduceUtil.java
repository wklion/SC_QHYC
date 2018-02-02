package com.spd.grid.tool;

import java.util.ArrayList;

public class ReduceUtil {
	public ArrayList<Double> SimpleReduce(ArrayList<Double> data){
		ArrayList<Double> result=new ArrayList<Double>();
		int size=data.size();
		if(size==0)
			return result;
		double preVal=data.get(0);
		double count=1;
		for(int i=1;i<size;i++)
		{
			double val=data.get(i);
			if(val==preVal)
			{
				count++;
			}
			else
			{
				result.add(preVal);
				result.add(count);
				preVal=val;
				count=1;
			}
			if(i==size-1)//最后一个
			{
				result.add(val);
				result.add(count);
			}
		}
		return result;
	}
}
