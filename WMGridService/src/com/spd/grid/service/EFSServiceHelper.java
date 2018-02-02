package com.spd.grid.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.spd.grid.tool.DateUtil;

public class EFSServiceHelper {
	public List<String> getDerfUVFile(Calendar cal,String path,String level){
		Calendar calRes = (Calendar) cal.clone();
		Calendar calEnd = (Calendar) cal.clone();
		calEnd.add(Calendar.MONTH, 2);
		calEnd.add(Calendar.DATE, -1);
		String[] types = {"U","V"};
		String nameFormat = "%s.atm.%s.%s-%s_prs%s_member.nc";
		String strResDate = DateUtil.format("yyyyMMdd", calRes);
		String strEndDate = DateUtil.format("yyyyMMdd", calEnd);
		List<String> lsFile = new ArrayList();
		for(String type:types){
			String fileName = String.format(nameFormat, strResDate,type,strResDate,strEndDate,level);
			String strFile = path + fileName;
			File file = new File(strFile);
			if(file.exists()){
				lsFile.add(strFile);
			}
		}
		return lsFile;
	}
}
