package com.spd.grid.Executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorsEx {
	
	 /**
     * 新创建一个堵塞队列  在调用offer 的时候 不是加入阻塞队列 而是直接准备运行状态
     * 
     * @param threadSize
     * @return
     */
    public static ExecutorService newFixedThreadPool(int threadSize) {
        return new ThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1) {

                    private static final long serialVersionUID = -9028058603126367678L;

                    @Override
                    public boolean offer(Runnable e) {
                        try {
                            put(e);
                            return true;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        return false;
                    }
                });
    }
	
	

}
