package com.spd.qhyc.test;

import com.spd.qhyc.app.ModelDayJP;
import com.spd.qhyc.app.ModelMonthJP;
import com.mg.objects.Workspace;

public class ModelDayJPTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Workspace ws = new Workspace();
		//ModelMonthJP modelJP = new ModelMonthJP();
		//modelJP.excute(ws, "E:/SC/Data/Temp/month/");
		ModelDayJP modelDayJP = new ModelDayJP();
		modelDayJP.excute(ws, "E:/SC/Data/Prec/day/");
		ws.Destroy();
	}

}
