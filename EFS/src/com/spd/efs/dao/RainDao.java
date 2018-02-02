package com.spd.efs.dao;

import java.util.List;

import com.spd.efs.pojo.Rain;

public interface RainDao {
     
	
	 //查询平均降水数据
	 public List<Rain> getRainTemAvgData();
	
	 //查询高原雨季开始期所需数据（自4月21日――6月30日）
	 public List<Rain> getPlateauRainSeasonStartTimeData();
	 
	 //查询所有站点
	 public List<Rain>  getAllStationData();
	 
}
