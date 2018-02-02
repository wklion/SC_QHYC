package com.spd.grid.exception;

import com.spd.grid.tool.LogTool;


public class NotFoundAnnotationException extends Exception{
	
	public NotFoundAnnotationException(String errorInfo){
		LogTool.logger.error(errorInfo);
		
	}
	

}
