package com.spd.grid.tool;

import com.mg.objects.Analyst;
import com.mg.objects.GeoPoint;
import com.mg.objects.GeoRegion;
import com.mg.objects.Workspace;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月4日
 * @DESCRIPTION:对象相关性
 */
public class GeoRel {
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月4日
	 * @RETURN:true或false
	 * @PARAM:ws-工作空间，gr-面对象，gp-点对象
	 * @DESCRIPTION:面中是否包含点
	 */
	public Boolean Contain(Workspace ws,GeoRegion gr,GeoPoint gp){
		Analyst pAnalyst = Analyst.CreateInstance("SpatialRel", ws);
		String strJson = String.format("\"Geometry\":\"%x\"", gr.GetHandle());
		pAnalyst.SetPropertyValue("A", "{" + strJson + "}");
		pAnalyst.SetPropertyValue("SpatialRel", "Contain"); //包含
		strJson = String.format("\"Geometry\":\"%x\"", gp.GetHandle());
		pAnalyst.SetPropertyValue("B", "{" + strJson + "}");
		pAnalyst.Execute();
		String op=pAnalyst.GetPropertyValue("Output");
		if(op.equals("true"))
			return true;
		else
			return false;
	}
}
