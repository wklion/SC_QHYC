package com.spd.grid.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.spd.grid.dao.BaseDao;
import com.spd.grid.dao.impl.BaseDaoImpl;
import com.spd.grid.domain.Area;

public class JDBCTest {
	
	/**
	 * 测试查询批量处理
	 * @throws Exception
	 */
	@Test
	public void testFindAllByConditions() throws Exception{
		BaseDao baseDao = new BaseDaoImpl();
		Map<String,Object> conditions = new HashMap<String, Object>(); 
		conditions.put("name", "未命名");
		List<Area> list = baseDao.findAllByConditions(conditions, Area.class);
		System.out.println(list.size());
	}

}
