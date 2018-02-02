package com.spd.grid.dao;

import java.util.List;

import com.spd.grid.domain.Area;

public interface AreaDao {
	
	
	/**
	 * 根据departCode获取关注区域
	 */
	
	public List<Area> getAreaByDepartCode(int departCode)throws Exception;
	
	public int countAreaByDepartCode(int departCode) throws Exception;
	
	public void addArea(Area area) throws Exception;
	
	public String findDepartCodeByUserName(String userName) throws Exception;
	
	public void updateAreaName(Area area) throws Exception;
	
	public Area getAreaById(int id) throws Exception;
	
	public List<Area> getAreaByType(int type) throws Exception;

}
