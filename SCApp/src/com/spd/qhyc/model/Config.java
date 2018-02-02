package com.spd.qhyc.model;
/**
 * @作者:wangkun
 * @日期:2017年12月5日
 * @公司:spd
 * @说明:
*/
public class Config {
	private int startYear;//开始年
	private String dataOutputPath;//数据输出目录
	private String monthPrecJPFileName;//月降水距平文件名
	private String monthTempJPFileName;//月气温距平文件名
	private String cimissUserID;//cimiss用户名
	private String cimissPassword;//cimiss密码
	private String areaCodes;//区域编码
	private String cimissHost;//主机
	private String hgtAvgPath;//平均高度场文件路径
	private String tempDatagramPath;//气温报文路径
	private String precDatagramPath;//降水报文路径
	private String modeHeightPath;//模式高度场路径
	private String liveHeightFile;//实况
	private String hosPrecFile;//历史降水文件
	private String hosTempFile;//历史气温文件
	private String hosMonthTempAvgPath;//历史月气温平均路径
	private String hosMonthPrecAvgPath;//历史月降水平均路径
	private String hosMonthTempPathMulFile;//历史月气温路径(多文件)
	private String hosMonthPrecPathMulFile;//历史月降水路径(多文件)
	private Boolean debug;//是否调试
	
	public String getDataOutputPath() {
		return dataOutputPath;
	}

	public void setDataOutputPath(String dataOutputPath) {
		this.dataOutputPath = dataOutputPath;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public String getMonthPrecJPFileName() {
		return monthPrecJPFileName;
	}

	public void setMonthPrecJPFileName(String monthPrecJPFileName) {
		this.monthPrecJPFileName = monthPrecJPFileName;
	}

	public String getMonthTempJPFileName() {
		return monthTempJPFileName;
	}

	public void setMonthTempJPFileName(String monthTempJPFileName) {
		this.monthTempJPFileName = monthTempJPFileName;
	}

	public String getCimissUserID() {
		return cimissUserID;
	}

	public void setCimissUserID(String cimissUserID) {
		this.cimissUserID = cimissUserID;
	}

	public String getCimissPassword() {
		return cimissPassword;
	}

	public void setCimissPassword(String cimissPassword) {
		this.cimissPassword = cimissPassword;
	}

	public String getAreaCodes() {
		return areaCodes;
	}

	public void setAreaCodes(String areaCodes) {
		this.areaCodes = areaCodes;
	}

	public String getCimissHost() {
		return cimissHost;
	}

	public void setCimissHost(String cimissHost) {
		this.cimissHost = cimissHost;
	}
	
	public String getHgtAvgPath() {
		return hgtAvgPath;
	}

	public void setHgtAvgPath(String hgtAvgPath) {
		this.hgtAvgPath = hgtAvgPath;
	}
	public String getTempDatagramPath() {
		return tempDatagramPath;
	}

	public void setTempDatagramPath(String tempDatagramPath) {
		this.tempDatagramPath = tempDatagramPath;
	}

	public String getPrecDatagramPath() {
		return precDatagramPath;
	}

	public void setPrecDatagramPath(String precDatagramPath) {
		this.precDatagramPath = precDatagramPath;
	}

	public String getModeHeightPath() {
		return modeHeightPath;
	}

	public void setModeHeightPath(String modeHeightPath) {
		this.modeHeightPath = modeHeightPath;
	}

	public String getLiveHeightFile() {
		return liveHeightFile;
	}

	public void setLiveHeightFile(String liveHeightFile) {
		this.liveHeightFile = liveHeightFile;
	}

	public String getHosPrecFile() {
		return hosPrecFile;
	}

	public void setHosPrecFile(String hosPrecFile) {
		this.hosPrecFile = hosPrecFile;
	}

	public String getHosTempFile() {
		return hosTempFile;
	}

	public void setHosTempFile(String hosTempFile) {
		this.hosTempFile = hosTempFile;
	}

	public String getHosMonthTempAvgPath() {
		return hosMonthTempAvgPath;
	}

	public void setHosMonthTempAvgPath(String hosMonthTempAvgPath) {
		this.hosMonthTempAvgPath = hosMonthTempAvgPath;
	}

	public String getHosMonthPrecAvgPath() {
		return hosMonthPrecAvgPath;
	}

	public void setHosMonthPrecAvgPath(String hosMonthPrecAvgPath) {
		this.hosMonthPrecAvgPath = hosMonthPrecAvgPath;
	}
	
	public String getHosMonthTempPathMulFile() {
		return hosMonthTempPathMulFile;
	}

	public void setHosMonthTempPathMulFile(String hosMonthTempPathMulFile) {
		this.hosMonthTempPathMulFile = hosMonthTempPathMulFile;
	}

	public String getHosMonthPrecPathMulFile() {
		return hosMonthPrecPathMulFile;
	}

	public void setHosMonthPrecPathMulFile(String hosMonthPrecPathMulFile) {
		this.hosMonthPrecPathMulFile = hosMonthPrecPathMulFile;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}
	
	
}
