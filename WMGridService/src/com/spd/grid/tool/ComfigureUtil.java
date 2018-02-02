package com.spd.grid.tool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.spd.grid.pojo.CommonConfig;
/**
 * @throws Exception 
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月16日
 * @RETURN:
 * @PARAM:
 * @DESCRIPTION:初始化配置文件
 */
public class ComfigureUtil {
	public static CommonConfig config=null;
	static{
		if(config==null){
			System.out.println("初始化配置文件!");
			ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext.xml");
			config=(CommonConfig) ac.getBean("commonConifg");
		}
	}
}
