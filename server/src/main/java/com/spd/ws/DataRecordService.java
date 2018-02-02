package com.spd.ws;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.tool.LogTool;
import com.spd.util.CommonConfig;

/**
 * 补录数据
 * @author Administrator
 *
 */
@Stateless
@Path("DataRecordService")
public class DataRecordService {

	private CommonConfig commonConfig = (CommonConfig)ContextLoader.getCurrentWebApplicationContext().getBean("commonConfig");
	
	/**
	 * 根据时间段补录数据
	 * @param para
	 * @return
	 */
	@POST
	@Path("recordByTimes")
	@Produces("application/json")
	public Object recordByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			jsonObject = new JSONObject(para);
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			long start = sdf1.parse(startTime).getTime();
			long end = sdf1.parse(endTime).getTime();
			for(long time = start; time <= end; time += CommonConstant.DAYTIMES) {
				String timeStr = sdf1.format(new Date(time));
				String result = execute(timeStr);
				if(result == null) {
					return "false";
				}
			}
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "false";
		}
	}
	
	private String execute(String timeStr) {
		Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象  
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {  
            Process p = run.exec("java -cp SPPMInternetResourceSync.jar com.spd.schedule.CIMISSDayExecutor " + timeStr, null, 
            		new File(commonConfig.getSyncPath()));// 启动另一个进程来执行命令  
            //半分钟没执行完的话，就关闭程序 
            in = new BufferedInputStream(p.getInputStream());  
            inBr = new BufferedReader(new InputStreamReader(in));  
            String lineStr;  
            while ((lineStr = inBr.readLine()) != null)  
                //获得命令执行后在控制台的输出信息  
                System.out.println(lineStr);// 打印输出信息  
            //检查命令是否执行失败。  
            if (p.waitFor() != 0) {  
                if (p.exitValue() != 0) { //p.exitValue()==0表示正常结束，1：非正常结束   
                    System.err.println("命令执行失败!");  
                    return null;
                }
            }  
            return "success";
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;
        }  finally {
        	try {
				inBr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
            try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
        }
	}
}
