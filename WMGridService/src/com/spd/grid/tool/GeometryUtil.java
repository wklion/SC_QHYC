package com.spd.grid.tool;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.mg.objects.GeoRegion;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月10日
 * @DESCRIPTION:几何对象相关
 */
public class GeometryUtil {
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月10日
	 * @RETURN:多个面对象
	 * @PARAM:strPoints--所有点
	 * @DESCRIPTION:把点转换多个对象
	 */
	public List<GeoRegion> ConvertPointsToRegions(String strPoints)
	{
		strPoints=strPoints.replace("[", "");
		strPoints=strPoints.replace("]", "");
		String[] strRegion=strPoints.split(",-1,");
		int regionCount=strRegion.length;
		List<GeoRegion> lsGeoRegion=new ArrayList<>();//全部面
		for(int i=0;i<regionCount;i++)
		{
			String re=strRegion[i];
			String[] regionPoints=re.split(",");
			int regionPointCount=regionPoints.length;
			int ptCount=regionPointCount/2;
    		Point2D[] p2ds=new Point2D[ptCount];
    		for(int j=0;j<ptCount;j++)
    		{
    			double lon=Double.parseDouble(regionPoints[j*2]);
    			double lat=Double.parseDouble(regionPoints[j*2+1]);
    			Point2D p2d=new Point2D.Double(lon,lat);
    			p2ds[j]=p2d;
    		}
    		GeoRegion geoRegion = new GeoRegion(p2ds);
    		lsGeoRegion.add(geoRegion);
		}
		return lsGeoRegion;
	}
}
