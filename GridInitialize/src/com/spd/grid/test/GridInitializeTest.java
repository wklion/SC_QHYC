package com.spd.grid.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.spd.domain.GridInfo;
import com.spd.grid.GridInitialize;

public class GridInitializeTest {

	@Test
	public void testAddGridInfo() {
		GridInitialize gridInitialize = new GridInitialize();
		List<GridInfo> gridproducts = new ArrayList<GridInfo>();
		for(int i=0; i<100; i++) {
			GridInfo gridproduct = new GridInfo();
			gridproduct.setDepartCode("departCode" + i);
			gridproduct.setType("type" + i);
			gridproduct.setElement("element" + i);
			gridproduct.setForecastTime("forecastTime" + i);
			gridproduct.setHourSpan(0);
			gridproduct.setTotalHourSpan(0);
			gridproduct.setLevel(0);
			gridproduct.setTabelName("tabelName" + i);
			gridproduct.setNWPModel("nwpModel" + i);
			gridproduct.setNWPModelTime("nwpModelTime" + i);
			if(i < 50) {
				gridproduct.setUserName("userName" + i + "aa");
			} else {
				gridproduct.setUserName("userName" + i);
			}
			
			gridproduct.setForecaster("forecaster" + i);
			gridproduct.setIssuer("issuer" + i);
			gridproduct.setMakeTime("makeTime" + i);
			gridproduct.setLastModifyTime("lastModifyTime" + i);
			gridproduct.setRemark("remark" + i);
			gridproducts.add(gridproduct);
		}
		gridInitialize.addGridInfo(gridproducts);
	}
}
