package com.spd.grid.tool;

import java.io.File;
import java.io.FilenameFilter;

public class ForecastfineFilter implements FilenameFilter {

	String para;
	
	public ForecastfineFilter(String para) {
		this.para = para;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		String filter = para;
		return name.matches(filter);
	}

}
