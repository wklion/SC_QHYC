package com.spd.datadeal;

import java.util.Calendar;

public class TaskMain {

	public static void main(String[] args) throws Throwable {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH, 4);
		cal.add(Calendar.DATE, -1);
		MonthData md = new MonthData();
		md.excute(cal);
	}

}
