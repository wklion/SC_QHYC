package com.spd.qhyc.test;

import com.spd.qhyc.app.ModelMonthJP;
import com.mg.objects.Workspace;

public class ModelJPTest {

	public static void main(String[] args) {
		Workspace ws = new Workspace();
		ModelMonthJP modelJP = new ModelMonthJP();
		modelJP.excute(ws, "E:/SC/Data/Prec/month/");
	}

}
