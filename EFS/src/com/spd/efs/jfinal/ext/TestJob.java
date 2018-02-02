package com.spd.efs.jfinal.ext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Scheduled(fixedDelay=1000*10)
public class TestJob implements Job{
 
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("TestJob任务执行了！");
    }
}
