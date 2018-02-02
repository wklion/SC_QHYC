package com.spd.qhyc.test;

public class ArrayTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] a = new double[6];
		a[3] = 5;
		add(a);
		System.out.println("");
	}
	public static void add(double[] arr) {
		for(int i=0,t=arr.length;i<t;i++) {
			arr[i] = arr[i]+3;
		}
	}
}
