package com.spd.grid.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionUtil {

	private static SqlSessionFactory sqlSessionFactory;
	
	private SqlSessionUtil() {
		
	}
	
	public static SqlSession getSqlSession() {
		if(sqlSessionFactory == null) {
			String resource = "resource/mybatis-config.xml";
			InputStream inputStream = null;
			try {
				inputStream = Resources.getResourceAsStream(resource);
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sqlSessionFactory.openSession();
	}
}
