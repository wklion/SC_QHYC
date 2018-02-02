package com.spd.grid.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.spd.grid.tool.IndexRead;

public class MJOTest {
	@Test
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndexRead RMMRead=new IndexRead();
		Calendar cal=Calendar.getInstance();
		Date dtStart=new Date();
		Date dtEnd=new Date();
		cal.set(2016, 7, 1);
		dtStart=cal.getTime();
		cal.set(2016, 8, 20);
		dtEnd=cal.getTime();
		Map<String,Double> result=RMMRead.GetRMMData("rmm1",dtStart,dtEnd);
		Calendar cc=Calendar.getInstance();
	}

}
