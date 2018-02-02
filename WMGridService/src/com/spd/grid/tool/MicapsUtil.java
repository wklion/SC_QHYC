package com.spd.grid.tool;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mg.objects.DatasetRaster;

public class MicapsUtil {
	
	
	/*
	 * 获取Micaps数据元数据
	 * */
	@SuppressWarnings("unused")
	public static Map<String, String> generateMicapsMetaData(DatasetRaster dr, String elementCaption, Date dateForecast, Integer hour,
			Double isolineInterval, Double isolineStart, Double isolineEnd){
		Calendar c = Calendar.getInstance();
		c.setTime(dateForecast);
		
		double tag = -1;  //倒过来
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("数据说明", elementCaption);
		metadata.put("年", String.valueOf(c.get(Calendar.YEAR)));
		metadata.put("月", String.valueOf(c.get(Calendar.MONTH)+1));
		metadata.put("日", String.valueOf(c.get(Calendar.DATE))); 
		metadata.put("时次", String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
		metadata.put("时效", String.valueOf(hour));
		metadata.put("层次", "0");
		metadata.put("经度格距", String.format("%.6f", dr.GetBounds().getWidth()/dr.GetWidth()));
		metadata.put("纬度格距", String.format("%.6f", tag*dr.GetBounds().getHeight()/dr.GetHeight()));
		metadata.put("起始经度", String.format("%.6f", dr.GetBounds().getX()));
		metadata.put("终止经度", String.format("%.6f", dr.GetBounds().getX() + dr.GetBounds().getWidth()));
		metadata.put("起始纬度", String.format("%.6f", tag>0?dr.GetBounds().getY() : dr.GetBounds().getY() + dr.GetBounds().getHeight()));
		metadata.put("终止纬度", String.format("%.6f", tag>0?dr.GetBounds().getY() + dr.GetBounds().getHeight() : dr.GetBounds().getY()));
		metadata.put("纬向格点数", String.valueOf(dr.GetWidth()));
		metadata.put("经向格点数", String.valueOf(dr.GetHeight()));
		metadata.put("等值线间隔", String.format("%.6f",isolineInterval));
		metadata.put("等值线起始值", String.format("%.6f",isolineStart));
		metadata.put("终止值", String.format("%.6f",isolineEnd));
		metadata.put("平滑系数", "1.000000");
 		metadata.put("加粗线值", "0.000000");
		return metadata;
	}
	
	
	

}
