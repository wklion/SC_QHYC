package com.spd.efs.schedule;

import net.sf.json.JSONObject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.spd.efs.common.CimissCommon;
import com.spd.efs.jfinal.ext.QuartzPlugin;
import com.spd.efs.jfinal.ext.Scheduled;
import com.spd.efs.jfinal.ext.TestJob;


@Scheduled(fixedDelay=1000*10)
public class SyncCimissTEMData implements Job{
    /**
     * 同步地面逐日温度数据
     */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		StringBuffer interfaceUrl = new StringBuffer();
		interfaceUrl.append("http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106");
		interfaceUrl.append("&interfaceId=getSurfEleByTime");
		interfaceUrl.append("&dataCode=SURF_CHN_MUL_DAY");
		interfaceUrl.append("&elements=Station_Name,Province,City,Town,Datetime,Station_Id_C");
		interfaceUrl.append("&times=20160317000000");
		interfaceUrl.append("&limitCnt=10");
		interfaceUrl.append("&dataFormat=json");
		String data = CimissCommon.callCIMISS(interfaceUrl.toString());
		JSONObject jsonObject = JSONObject.fromObject(data);
		String result = jsonObject.getString("DS");
		System.out.println(result);
		
	}
	
	
	public static void main(String [] args){
		QuartzPlugin plugin = new QuartzPlugin(SyncCimissTEMData.class);
		plugin.start();
		
	}
	
	
	

}
