
package com.spd.grid.ws;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;
import com.spd.grid.domain.Application;
import com.spd.grid.funModel.CheckResStatusParam;
import com.spd.grid.funModel.GetResLastDateParam;
import com.spd.grid.model.CommonResult;
import com.spd.grid.tool.DateFormat;
import com.spd.grid.tool.MyFilter;


@Stateless
@Path("FileInfo")
public class FileInfo {
	
	@POST
	@Path("getResDate")
	@Produces("application/json")
	public Object getResDate(@FormParam("para") String para) throws Exception{
//		List list=new ArrayList();
		List<String> result = new ArrayList();
		String path = "";
		String unit = "";
		String slet = "";
		try {
			JSONObject jo = new JSONObject(para);
			path = jo.getString("path");
			unit = jo.getString("unit");
			slet = jo.getString("slet");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Workspace ws=Application.m_workspace;
		File fileDir = new File(path);
		if(!fileDir.exists()){
			return null;
		}
		MyFilter myFilter = new MyFilter(".nc");
		File[] files = fileDir.listFiles(myFilter);	
		File newFile = files[files.length-1];
		if(!"0".equals(slet))
		{
			String a;
	        for(int i = 0;i<files.length;i++)
	        {
	        	a=files[i].getName().substring(0,6);
	        	if(a.equals(slet))
	        	{
	        		newFile=files[i];
	        		break;
	        	}
	        }
		}
		String alias = newFile.getName();
		String strDate = alias.substring(0, 8);
		Date date = DateFormat.yyyyMMdd.parse(strDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String strFile = newFile.getAbsolutePath().replace("\\", "/");
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", alias,strFile);
		Datasource ds = ws.OpenDatasource(strJson);
		int curIndex = -1;
		for(int i=0,dsCount = ds.GetDatasetCount();i<dsCount;i++){
			DatasetRaster dr = (DatasetRaster) ds.GetDataset(i);
			String msg = dr.GetMetadata();
			JSONObject jo = new JSONObject(msg);
			int timeIndex = Integer.parseInt(jo.getString("NETCDF_DIM_time"));
			if(timeIndex==curIndex){//相同时效
				continue;
			}
			String curStrDate = "";
			if(unit.equals("day")){
				curStrDate = DateFormat.yyyyMMdd.format(cal.getTime());
				cal.add(Calendar.DATE, (timeIndex-curIndex));
			}
			else{
				curStrDate = DateFormat.yyyyMM.format(cal.getTime());
				cal.add(Calendar.MONTH, (timeIndex-curIndex));
			}
			result.add(curStrDate);
			curIndex = timeIndex;
		}
//	    list.add(result);
		return result;
	}
	@POST
    @Path("getModeResLastDate")
    @Produces("application/json")
    public Object getModeResLastDate(@FormParam("para") String para) throws Exception{
	    CommonResult cr = new CommonResult();
	    Gson gson = new Gson();
	    GetResLastDateParam getModeResLastDate = gson.fromJson(para, GetResLastDateParam.class);
	    String path = getModeResLastDate.getPath();
	    File fileDir = new File(path);
        if(!fileDir.exists()){
            cr.setErr(path+"目录不存在!");
            return cr;
        }
        MyFilter myFilter = new MyFilter(".nc");
        File[] files = fileDir.listFiles(myFilter);
        if(files.length<1){
            cr.setErr("没有找到nc文件!");
            return cr;
        }
        File newFile = files[files.length-1];
        String strFileName = newFile.getName();
        String strDate = strFileName.substring(0,8);
        cr.setSuc(strDate);
        return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月17日
	 * @修改日期:2018年1月17日
	 * @参数:file-文件路径
	 * @返回:true或false
	 * @说明:获取资料状态
	 */
	@POST
    @Path("checkResStatus")
    @Produces("application/json")
    public Object checkResStatus(@FormParam("para") String para){
		CommonResult cr = new CommonResult();
	    Gson gson = new Gson();
	    CheckResStatusParam checkResStatusParam = gson.fromJson(para, CheckResStatusParam.class);
	    String strFile = checkResStatusParam.getFile();
	    File file = new File(strFile);
	    if(file.exists()){
	    	cr.setSuc(true);
	    }
	    else{
	    	cr.setSuc(false);
	    }
	    return cr;
	}
}
