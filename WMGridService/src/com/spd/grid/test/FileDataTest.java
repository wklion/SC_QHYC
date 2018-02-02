package com.spd.grid.test;

import com.spd.grid.ws.FileData;

public class FileDataTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getGrid();
	}
	private static void getGrid(){
		String param = "{path:'E:/SC/Data/Prec/month/',hourspan:'1'}";
		FileData fileData = new FileData();
		fileData.getGrid(param);
	}
}
