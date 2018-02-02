package com.spd.test;

import java.io.File;

import com.spd.tool.MD5Tool;

public class MD5ToolTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MD5Tool md5Tool = new MD5Tool();
		File file = new File("F:/Server/Server平台/重庆/重庆气象数据支撑平台/视频");
		File[] items = file.listFiles();
		for(File item:items) {
			String md5Str = md5Tool.getFileMD5String(item);
			System.out.println(md5Str + "," + item.getName());
		}
	}

}
