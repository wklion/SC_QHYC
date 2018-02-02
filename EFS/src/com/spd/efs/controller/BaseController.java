package com.spd.efs.controller;

import com.jfinal.core.Controller;

public class BaseController extends Controller{
	
	protected String ctx;
	

	/**
	 * 复写render 方法
	 */
	public void render(String view){
		
		setAttr("ctx", getCtx());
		super.render(view);
		
		
	}
	
	
	
	/**
	 * 服务页面工程路径全局变量
	 * @return
	 */
	public String getCtx(){
		String contentPath = getRequest().getContextPath();
		String scheme =  getRequest().getScheme();
		String host =    getRequest().getRemoteHost();
		Integer port = getRequest().getLocalPort();
		ctx = scheme + "://" + "127.0.0.1" + ":"+ port+contentPath;
		return ctx;
	}

}
