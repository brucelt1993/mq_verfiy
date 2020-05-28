package com.iwhalecloud.verfication.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 20:47
 **/
@Configuration
@ConfigurationProperties(prefix = "ctgmq.thread")
public class CtgMqThreadConfig {
    private int corePoolSize;
    private int maximumPoolSize;
    private int queueSize;
    private long executeTime;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public CtgMqThreadConfig setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public CtgMqThreadConfig setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public CtgMqThreadConfig setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public CtgMqThreadConfig setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
        return this;
    }
}
