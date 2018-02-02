package com.spd.qhyc.other;

import java.io.File;
import java.io.FileFilter;

/**
 * @作者:wangkun
 * @日期:2017年11月8日
 * @公司:spd
 * @说明:
*/
public class EndFilter implements FileFilter {
	private String formater;
	public EndFilter(String str){
		this.formater = str;
	}
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()){
			return true;
		}
		else{
			String name = file.getName();
			if(name.endsWith(formater))
		                return true;  
		            else  
		                return false;  
		}
	}
}
