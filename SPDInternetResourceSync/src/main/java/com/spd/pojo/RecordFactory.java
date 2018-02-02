package com.spd.pojo;

import java.io.File;
import java.io.FileInputStream;

import com.spd.tool.LogTool;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class RecordFactory {

	private static Record record;
	
	private RecordFactory() {
		
	}
	
	public synchronized static Record getRecord() {
		if(record != null) {
			return record;
		}
		XStream xStream = new XStream(new DomDriver());
		//解析分三块内容，1.节点
		xStream.alias("Record", Record.class);
		xStream.alias("RecordItem", RecordItem.class);
		//2.内容中的List
		xStream.addImplicitCollection(Record.class, "records");
		//3.属性信息
		try {   
	        FileInputStream ops = new FileInputStream(new File("config/Record.xml"));   
	        record = (Record)xStream.fromXML(ops);   
	        ops.close();   
        } catch (Exception e) {   
        	LogTool.logger.error("Record.xml 解析失败，详情:" + e.getMessage());
        }
        return record;
	}
}
