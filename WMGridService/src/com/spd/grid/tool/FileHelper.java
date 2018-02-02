package com.spd.grid.tool;

import java.io.File;

public class FileHelper {
	public void readFile(String strFile){
		File file = new File(strFile);
		if(!file.exists()){
			LogTool.logger.error("文件"+strFile+"不存在!");
			return;
		}
	}
}
