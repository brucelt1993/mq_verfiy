package com.iwhalecloud.verfication.mq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luotuan
 * @Description 生产者线程池
 * @create 2020-05-25 20:44
 **/
public class ProducerThreadPool extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ProducerThreadPool.class);
    private static final ConcurrentHashMap<String, LocalDateTime> startTime = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> costMap = new ConcurrentHashMap<>();
    private String poolName;

    public ProducerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.poolName = poolName;
    }

    public ProducerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.poolName = poolName;
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (logger.isInfoEnabled()) {
            logger.info("{} 线程池开始关闭，已完成的任务：{},正在执行的任务：{},代办的任务：{}",
                    this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        }
        clear();
        return super.shutdownNow();
    }

    @Override
    public void shutdown() {
        if (logger.isInfoEnabled()) {
            logger.info("{} 线程池开始关闭，已完成的任务：{},正在执行的任务：{},代办的任务：{}",
                    this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        }
        clear();
        super.shutdown();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTime.put(String.valueOf(r.hashCode()), LocalDateTime.now());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        LocalDateTime startDateTime = startTime.remove(String.valueOf(r.hashCode()));
        LocalDateTime finishDateTime = LocalDateTime.now();
        Duration duration = Duration.between(startDateTime, finishDateTime);
        costMap.put(String.valueOf(r.hashCode()), duration.toMillis());
    }

    public void clear() {
        startTime.clear();
        costMap.clear();
    }
}
