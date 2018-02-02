package com.spd.grid;

public class MonitorThread extends Thread {

	private long start = System.currentTimeMillis();
	/**
	 * 守护线程，用于监控系统运行状态。
	 */
	private int time;
	
	public MonitorThread(int time) {
		this.time = time;
	}
	
	public void run() {
		while(true) {
			long current = System.currentTimeMillis();
			if(current - start >= time * 60 * 1000) {
//			if(current - start >= 1 * 1000) {
				System.err.println("force exit............");
				System.exit(0);
			}
			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
