package com.spd.tool;

import java.io.File;
import java.io.FilenameFilter;

public class MCIFileFilter implements FilenameFilter {

	private String filterName;
	
	public MCIFileFilter(String filterName) {
		this.filterName = filterName;
	}
	
	public boolean accept(File dir, String name) {
		return name.startsWith(filterName);
	}

}
