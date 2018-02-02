package com.spd.grid.model;
/**
 * @作者:wangkun
 * @日期:2017年7月22日
 * @公司:spd
 * @说明:配置文件信息
*/
public class Config {
	private String monthHgtFile;//月高度场文件
	private String osrFile;//最优子集程序
	private String hgtMonthAvgPath;//高度场月平均tif所在目录
	private String hgtMonthJPPath;//高度场月距平tif所在目录
	private String derfHgtMonthPath;//derf高度场所在目录
	private String stationFile;//站点Json文件
	private String factorPath;//因子路径
	private String modeJPPath;//模式距平文件路径
	private String modeHgtPath;//模式高度场路径
	private String hosMonthTempAvgPath;//历史观测月平均气温路径
	private String hosMonthPrecAvgPath;//历史观测月平均降水路径
	public String getFactorPath() {
		return factorPath;
	}

	public void setFactorPath(String factorPath) {
		this.factorPath = factorPath;
	}

	public String getStationFile() {
		return stationFile;
	}

	public void setStationFile(String stationFile) {
		this.stationFile = stationFile;
	}

	public String getDerfHgtMonthPath() {
		return derfHgtMonthPath;
	}

	public void setDerfHgtMonthPath(String derfHgtMonthPath) {
		this.derfHgtMonthPath = derfHgtMonthPath;
	}

	public String getHgtMonthJPPath() {
		return hgtMonthJPPath;
	}

	public void setHgtMonthJPPath(String hgtMonthJPPath) {
		this.hgtMonthJPPath = hgtMonthJPPath;
	}

	public String getOsrFile() {
		return osrFile;
	}

	public String getHgtMonthAvgPath() {
		return hgtMonthAvgPath;
	}

	public void setHgtMonthAvgPath(String hgtMonthAvgPath) {
		this.hgtMonthAvgPath = hgtMonthAvgPath;
	}

	public void setOsrFile(String osrFile) {
		this.osrFile = osrFile;
	}
	public String getMonthHgtFile() {
		return monthHgtFile;
	}

	public void setMonthHgtFile(String monthHgtFile) {
		this.monthHgtFile = monthHgtFile;
	}

	public String getModeJPPath() {
		return modeJPPath;
	}

	public void setModeJPPath(String modeJPPath) {
		this.modeJPPath = modeJPPath;
	}

    public String getModeHgtPath() {
        return modeHgtPath;
    }

    public void setModeHgtPath(String modeHgtPath) {
        this.modeHgtPath = modeHgtPath;
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
	
}
