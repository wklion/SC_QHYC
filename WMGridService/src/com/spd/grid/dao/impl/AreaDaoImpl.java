package com.spd.grid.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.grid.dao.AreaDao;
import com.spd.grid.dao.BaseDao;
import com.spd.grid.domain.Area;
import com.spd.grid.domain.Station;

public class AreaDaoImpl implements AreaDao{
    BaseDao<Area> dao = new BaseDaoImpl<Area>();
    BaseDao<Station> daoStation = new BaseDaoImpl<Station>();
	@Override
	public int countAreaByDepartCode(int departCode) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("departCode", departCode);
		return dao.findAllByConditions(condition, Area.class).size();
	}

	@Override
	public List<Area> getAreaByDepartCode(int departCode) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("departCode", departCode);
		return dao.findAllByConditions(condition, Area.class);
	}

	@Override
	public void addArea(Area area) throws Exception {
		dao.save(area);
		
	}

	@Override
	public String findDepartCodeByUserName(String userName) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("createUser", userName);
		return dao.findAllByConditions(condition, Area.class).get(0).getDepartCode();
	     
	}

	@Override
	public void updateAreaName(Area area) throws Exception {
		dao.update(area);
		
	}

	@Override
	public Area getAreaById(int id) throws Exception {
		return dao.get(id, Area.class);
	}

	@Override
	public List<Area> getAreaByType(int type) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		return dao.findAllByConditions(condition, Area.class);
	}
}
