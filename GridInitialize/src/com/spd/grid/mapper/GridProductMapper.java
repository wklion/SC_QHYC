package com.spd.grid.mapper;

import java.util.List;

import com.spd.domain.GridInfo;

public interface GridProductMapper {
	
	public void addGridProducts(List<GridInfo> gridproducts);

	public void deleteGridProducts(List<GridInfo> gridproducts);
	
}
