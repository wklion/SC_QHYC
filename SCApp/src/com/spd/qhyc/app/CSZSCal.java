package com.spd.qhyc.app;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.util.DateUtil;
import com.spd.qhyc.util.LocalDatagram;

public class CSZSCal {
	static Logger logger = LogManager.getLogger("mylog");
	private static  int forcastDay = 30;//预报天数
	final static double minTemp=4.0;
	final static double minRain=10.0;
	static DecimalFormat df   = new DecimalFormat("#0.00");
	public static void main(String[] args) throws Exception {
		//1、初始化时间
		Calendar cal = Calendar.getInstance();
		if(args.length>0) {//指定时间
			String strDate = args[0];
			if(strDate.length() == 10) {
				cal = DateUtil.parse("yyyy-MM-dd", strDate);
			}
		}
		cal.add(Calendar.MONTH, -2);//处理最近1个月数据
		// 2、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// 3、获取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		int totalDay = 30;
		while(totalDay>0) {
			String strDate = DateUtil.format("yyyy-MM-dd", cal);
			System.out.println("处理资料时间:"+strDate);
			dealData(cal,dpConn,config);
			cal.add(Calendar.DATE, 1);
			totalDay--;
		}
		dpConn.close();
		logger.info("全部执行完成");
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:cal-数据日期
	 * @返回:
	 * @说明:处理数据
	 */
	private static void dealData(Calendar cal,DruidPooledConnection dpConn,Config config) throws Exception {
		Calendar thisCal = (Calendar) cal.clone();
		String strCurDate = DateUtil.format("yyyyMMdd", cal);
		Map<String,double[]> stationDataOfTemp = getTecent30DayDataFromDB(thisCal,dpConn,"气温");
		Map<String,double[]> stationDataOfPrec = getTecent30DayDataFromDB(thisCal,dpConn,"降水");
		System.out.println("气温");
		//获取本地温度报文
		LocalDatagram ld=new LocalDatagram();
		Map<String,String> tempMap=ld.AnalysisDatagram(config.getTempDatagramPath(),thisCal);
		if(tempMap.size() == 0){
			System.out.println(strCurDate+"日降温报文不存在或为空!");
			return;
		}
		HashMap hmTempZS=new HashMap();//ZS检验
		HashMap hmTempCS=new HashMap();//CS检验
		for(String stationNum:tempMap.keySet()) {
			String BaoWenVal = tempMap.get(stationNum);
			double zsResult=0.0;
			double csResult=0.0;
			if(BaoWenVal!=null){
				double[] vals = stationDataOfTemp.get(stationNum);
				zsResult=ZSCheckForTemp(vals,BaoWenVal,thisCal);
				csResult=CSCheckForTemp(vals,BaoWenVal,thisCal);
			}
			hmTempZS.put(stationNum, zsResult);
			hmTempCS.put(stationNum, csResult);
			System.out.println(stationNum+":ZS="+zsResult+",CS="+csResult);
		}
		System.out.println("降水");
		Map<String,String> precMap = ld.AnalysisDatagram(config.getPrecDatagramPath(),cal);
		HashMap hmRainZS=new HashMap();
		HashMap hmRainCS=new HashMap();
		for(String stationNum:precMap.keySet()) {
			String BaoWenVal = precMap.get(stationNum);
			double zsResult=0.0;
			double csResult=0.0;
			if(BaoWenVal!=null){
				double[] vals = stationDataOfPrec.get(stationNum);
				if(vals == null) {
					continue;
				}
				zsResult = ZSCheckForRain(vals,BaoWenVal,thisCal);
				csResult = CSCheckForRain(vals,BaoWenVal,thisCal);
			}
			hmRainZS.put(stationNum, zsResult);
			hmRainCS.put(stationNum, csResult);
			System.out.println(stationNum+":ZS="+zsResult+",CS="+csResult);
		}
		//入库前检查，先把这天的删掉，这样更方便，更新麻烦一些
		String strSql="delete from t_zscs where publictime="+strCurDate;
		PreparedStatement pss=dpConn.prepareStatement(strSql);
		pss.execute();
		pss.close();
		//入库
		dpConn.setAutoCommit(false);
		strSql="insert into t_zscs(publictime,stationnum,tzs,tcs,rzs,rcs) values(?,?,?,?,?,?)";
		PreparedStatement ps=dpConn.prepareStatement(strSql);
		Iterator it = hmTempZS.keySet().iterator();
		while(it.hasNext()){
			String sn = (String)it.next(); 
			Double tzsVal=(Double) hmTempZS.get(sn);
			Double tcsVal=(Double) hmTempCS.get(sn);
			Double rzsVal = (Double) hmRainZS.get(sn);
			Double rcsVal = (Double) hmRainCS.get(sn);
			//准备sql
			ps.setString(1, strCurDate);
			ps.setString(2, sn);
			ps.setDouble(3, tzsVal);
			ps.setDouble(4, tcsVal);
			ps.setDouble(5, rzsVal == null?0.0:rzsVal);
			ps.setDouble(6, rcsVal == null?0.0:rcsVal);
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
		dpConn.setAutoCommit(true);
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:获取最近30天数据
	 */
	private static Map<String,double[]> getTecent30DayDataFromDB(Calendar cal,DruidPooledConnection dpConn,String elementName) throws Exception {
		//结束结束时间，开始时间的30天后
		Calendar calStart = (Calendar) cal.clone();
		Calendar calEnd = (Calendar) cal.clone();
		calEnd.add(Calendar.DATE, 30);
		//组装查询字段
		String strSql="";
		if(elementName.equals("气温")){
			strSql="select %s from t_tem_avg where year=%d";
		}
		else if(elementName.equals("降水")){
			strSql="select %s from t_pre_time_0808";
		}
		String fields="STATION_ID_C,";
		while(calStart.getTime().before(calEnd.getTime())){
			String curField="m"+String.format("%02d", calStart.get(Calendar.MONTH)+1)+"d"+String.format("%02d", calStart.get(Calendar.DATE));
			fields+=curField+",";
			calStart.add(Calendar.DATE, 1);//加1天
		}
		fields=fields.substring(0, fields.length()-1);
		int year = cal.get(Calendar.YEAR);
		strSql=String.format(strSql, fields,year);
		PreparedStatement ps = dpConn.prepareStatement(strSql);
		ResultSet rs = ps.executeQuery();
		Map<String,double[]> stationData = new HashMap();
		while (rs.next()) {
			String stationNum = rs.getString(1);
			double[] data = new double[30];
			for(int i = 0;i < 30;i++) {
				data[i] = rs.getDouble(i+2);
			}
			stationData.put(stationNum, data);
		}
		return stationData;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:气温ZS
	 */
	public static double ZSCheckForTemp(double[] tempData,String strBaoWen,Calendar cal) throws Exception{
		double result=0;//结果值
		int rightCount=0;//正确的过程数
		int spaceCount=0;//空报过程数
		int louBaoCount=0;//漏报过程数
		int liveCount=0;//实况预报强降次数
		long startTime=cal.getTimeInMillis();//以30天前作为开始时间
		//得到实况强降次数
		double preVal=tempData[0];//前一天数据
		double startLiveVal= tempData[0];//开始天数据
		boolean flag=false;
		for(int f=1;f<forcastDay;f++){
			double curVal= tempData[f];
			double cha=curVal-preVal;
			if(cha<-1){//降温,每天降温应大于1
				flag=true;
			}
			else{//升温
				if(flag){
					if((startLiveVal-preVal)>minTemp){
						liveCount++;
					}
					flag=false;
				}
				startLiveVal=curVal;
			}
			preVal=curVal;
		}
		strBaoWen=strBaoWen.trim();//去除2头的空格
		String[] BaoWens=strBaoWen.split("\\s+");
		int forcastCount=Integer.parseInt(BaoWens[2]);//强降次数
		for(int i=0;i<forcastCount;i++){
			int firstIndex=2+3*i;
			String strStart=BaoWens[firstIndex+1];//开始时间
			String strEnd=BaoWens[firstIndex+2];//结束时间
			Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
			Calendar calEndDate = DateUtil.parse("yyyyMMdd", strEnd);
			int startIndex=(int) ((calStartDate.getTimeInMillis()-startTime)/(24*60*60*1000))+2;
			int endIndex=(int) ((calEndDate.getTimeInMillis()-startTime)/(24*60*60*1000))+2;
			if(endIndex>forcastDay){
				break;
			}
			if(startIndex==1&&endIndex==1){//第一天不参与，即不认为对，也不认为错
				
			}
			else{
				double cha = tempData[startIndex-1] - tempData[endIndex-1];
				if(cha>4.0){
					rightCount++;
				}
				else{
					spaceCount++;
				}
			}
		}
		//计算漏报次数
		double flCha=liveCount-forcastCount;
		if(flCha>0){
			louBaoCount=flCha==-1?1:2;	
		}
		if((rightCount+spaceCount+louBaoCount)!=0){
			result=(float)(rightCount)/(float)(rightCount+spaceCount+louBaoCount);
			result=Double.parseDouble(df.format(result));//精确到2位小数
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:气温CS检验
	 */
	public static double CSCheckForTemp(double[] tempData,String strBaoWen,Calendar cal) throws Exception{
		double result=0;//不计算的值
		int rightCount=0;//正确的过程数
		int spaceCount=0;//空报过程数
		int louBaoCount=0;//漏报过程数
		//以30天前作为开始时间
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(new Date());
		tempCal.set(Calendar.HOUR_OF_DAY, 0);//比较时间是把时分秒全置0
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.add(Calendar.DAY_OF_MONTH,-forcastDay);
		long startTime=cal.getTimeInMillis();
		strBaoWen=strBaoWen.trim();//去除2头的空格
		String[] BaoWens=strBaoWen.split("\\s+");
		int forcastCount=Integer.parseInt(BaoWens[2]);//强降次数
		Boolean bForcastMul=false;//预报次数-1
		//计算正确预报和空报次数
		for(int i=0;i<forcastCount;i++){
			int firstIndex=2+3*i;
			String strStart=BaoWens[firstIndex+1];//开始时间
			String strEnd=BaoWens[firstIndex+2];//结束时间
			Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
			Calendar calEndDate = DateUtil.parse("yyyyMMdd", strEnd);
			int startIndex=(int) ((calStartDate.getTimeInMillis()-startTime)/(24*60*60*1000))+2;
			int endIndex=(int) ((calEndDate.getTimeInMillis()-startTime)/(24*60*60*1000)) + 2;
			if(endIndex>forcastDay){//
				break;
			}
			Boolean isRightForcast=true;//是否正确预报
			Boolean firstUsed=false;//可以左右浮动一天，此标志表示第一次是否使用过
			if(startIndex==1&&endIndex==1){//第一天，由于不知道前一天，这里不参与计算
				bForcastMul=true;
			}
			else{
				for(int si=startIndex;si<endIndex+1;si++){
					double cha = tempData[startIndex-1] - tempData[endIndex-1];
					if(cha<minTemp){
						if(!firstUsed){
							firstUsed=true;
						}
						else{
							isRightForcast=false;
							break;
						}
					}
				}
				if(startIndex==endIndex&&firstUsed){
					isRightForcast=false;
				}
				if(isRightForcast){
					rightCount++;
				}
				else{
					spaceCount++;
				}
			}
		}
		if(bForcastMul){//预报次数是否减1
			forcastCount=forcastCount-1;
		}
		
		//计算漏报次数
		if(rightCount!=0){//正确次数为零，结果也为零，就不用计算了
			Boolean bStart=false;
			Date sTime=null;//开始时间
			Date eTime=null;//结束时间
			double preVal=tempData[0];
			tempCal.add(Calendar.DAY_OF_MONTH,1);//加2天，第一个计时用第二天的
			for(int d=1;d<forcastDay;d++){
				double curVal=tempData[d];
				double cha=preVal-curVal;
				if(cha>minTemp){
					if(!bStart){
						sTime=tempCal.getTime();
						bStart=true;
					}
				}
				else{
					if(bStart){//结束时间，前一天
						Boolean bLouBao=true;
						int i=bForcastMul==true?1:0;
						for(;i<forcastCount;i++){
							int firstIndex=2+3*i;
							String strStart=BaoWens[firstIndex+1];//开始时间
							String strEnd=BaoWens[firstIndex+2];//结束时间
							Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
							Calendar calEndDate = DateUtil.parse("yyyyMMdd", strEnd);
							if((sTime.getTime()-calStartDate.getTimeInMillis())>-1000&&(calStartDate.getTimeInMillis()-sTime.getTime())>-1000){//对于日期有一定误差
							}
							else{
								bLouBao=false;
								break;
							}
						}
						if(bLouBao){
							louBaoCount++;
						}
						bStart=false;
					}
				}
				preVal=curVal;//把这个值赋给前一个值
				eTime=tempCal.getTime();
				tempCal.add(Calendar.DAY_OF_MONTH,1);
			}
		}
		if((rightCount+spaceCount+louBaoCount)!=0){
			result=(float)(rightCount)/(float)(rightCount+spaceCount+louBaoCount);
			result=Double.parseDouble(df.format(result));//精确到2位小数
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:降水ZS检验
	 */
	public static double ZSCheckForRain(double[] precData,String strBaoWen,Calendar cal) throws Exception{
		double result=0;//不计算的值
		int rightCount=0;//正确的过程数
		int spaceCount=0;//空报过程数
		int louBaoCount=0;//漏报过程数
		int liveCount=0;//实况预报强降次数
		double totalRain=0;//总降雨
		long startTime=cal.getTimeInMillis();
		
		for(int f=0;f<forcastDay;f++){
			double curVal= precData[f];
			if(curVal>0.1){
				totalRain+=curVal;
			}
			else{//没降水，结算上次降水
				if(totalRain>minRain){
					liveCount++;
				}
			}
		}
		strBaoWen=strBaoWen.trim();
		String[] BaoWens=strBaoWen.split("\\s+");
		int forcastCount=Integer.parseInt(BaoWens[2]);//强降次数
		for(int i=0;i<forcastCount;i++){
			int firstIndex=2+3*i;
			String strStart=BaoWens[firstIndex+1];//开始时间
			String strEnd=BaoWens[firstIndex+2];//结束时间
			Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
			Calendar calEndDate = DateUtil.parse("yyyyMMdd", strEnd);
			int startIndex=(int) ((calStartDate.getTimeInMillis()-startTime)/(24*60*60*1000)) + 2;
			int endIndex=(int) ((calEndDate.getTimeInMillis()-startTime)/(24*60*60*1000)) + 2;
			if(endIndex>forcastDay){//
				break;
			}
			double sum=0;
			for(int c=startIndex;c<endIndex;c++){//粗略计算
				sum += precData[c];
			}
			if(sum>minRain){
				rightCount++;
			}
			else{
				spaceCount++;
			}
		}
		//计算漏报次数
		int flCha=liveCount-forcastCount;
		if(flCha>0){
			louBaoCount=flCha==-1?1:2;	
		}
		if((rightCount+spaceCount+louBaoCount)!=0){
			result=(float)(rightCount)/(float)(rightCount+spaceCount+louBaoCount);
			result=Double.parseDouble(df.format(result));//精确到2位小数
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:降水CS检验
	 */
	public static double CSCheckForRain(double[] precData,String strBaoWen,Calendar cal) throws Exception{
		double result=0;//不计算的值
		int rightCount=0;//正确的过程数
		int spaceCount=0;//空报过程数
		int louBaoCount=0;//漏报过程数
		Calendar startCal = (Calendar) cal.clone();
		Calendar endCal = (Calendar) cal.clone();
		long startTime = startCal.getTimeInMillis();
		//以30天前作为开始时间
		endCal.add(Calendar.DATE,-forcastDay);
		
		strBaoWen=strBaoWen.trim();//去除2头的空格
		String[] BaoWens=strBaoWen.split("\\s+");
		int forcastCount=Integer.parseInt(BaoWens[2]);//强降次数
		//计算正确预报和空报次数
		Boolean bForcastMul=false;//预报次数-1
		for(int i=0;i<forcastCount;i++){
			int firstIndex=2+3*i;
			String strStart=BaoWens[firstIndex+1];//开始时间
			String strEnd=BaoWens[firstIndex+2];//结束时间
			Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
			Calendar calEndDate = DateUtil.parse("yyyyMMdd", strEnd);
			int startIndex=(int) ((calStartDate.getTimeInMillis() - startTime)/(24*60*60*1000)) + 2;
			int endIndex=(int) ((calEndDate.getTimeInMillis() - startTime)/(24*60*60*1000)) + 2;
			if(endIndex>forcastDay){//报文可能超出我们预报的天数
				break;
			}
			Boolean isRightForcast=true;//是否正确预报
			Boolean firstUsed=false;//可以左右浮动一天，此标志表示第一次是否使用过
			for(int si=startIndex;si<endIndex+1;si++){
				double val = precData[si];
				if(val<10){
					if(!firstUsed){
						firstUsed=true;
					}
					else{
						isRightForcast=false;
						break;
					}
				}
			}
			if(firstUsed&&startIndex==endIndex){
				spaceCount++;
			}
			else if(firstUsed&&!isRightForcast){
				spaceCount++;
			}
			else{
				rightCount++;
			}
		}
		//计算漏报次数
		if(rightCount!=0){
			Date sTime=null;//开始时间
			//Date eTime=null;//结束时间
			Boolean bStart=false;
			for(int d=0;d<forcastDay;d++){
				double val = precData[d];
				if(val>minRain){
					if(!bStart){
						sTime=cal.getTime();
						bStart=true;
					}
				}
				else{//不是大于minRain
					if(bStart){//结算这一过程，结束时间为昨天
						Boolean bLouBao=true;
						for(int i=0;i<forcastCount;i++){
							int firstIndex=2+3*i;
							String strStart=BaoWens[firstIndex+1];//开始时间
							String strEnd=BaoWens[firstIndex+2];//结束时间
							Calendar calStartDate = DateUtil.parse("yyyyMMdd", strStart);
							if((sTime.getTime()-calStartDate.getTimeInMillis())>-1000&&(calStartDate.getTimeInMillis()-sTime.getTime())>-1000){//对于日期有一定误差
							}
							else{
								bLouBao=false;
								break;
							}
						}
						if(bLouBao){
							louBaoCount++;
						}
						bStart=false;
					}
				}
				//eTime=tempCal.getTime();
				//tempCal.add(Calendar.DAY_OF_MONTH,1);
			}
		}
		if((rightCount+spaceCount+louBaoCount)!=0){
			result=(float)(rightCount)/(float)(rightCount+spaceCount+louBaoCount);
			result=Double.parseDouble(df.format(result));//精确到2位小数
		}
		return result;
	}
}
