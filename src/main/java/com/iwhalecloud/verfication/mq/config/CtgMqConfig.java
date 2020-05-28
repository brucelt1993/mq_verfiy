package com.iwhalecloud.verfication.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 10:21
 **/
@Configuration
@ConfigurationProperties(prefix = "ctgmq.client")
public class CtgMqConfig {
    private String producerGroupName;
    private String namesrvAddr;
    private String authId;
    private String authPwd;
    private String instanceName;
    private String tenantId;
    private int producerNum;
    /**
     * 队列数
     */
    private int defaultTopicQueueNums;
    private String clusterName;

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public CtgMqConfig setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
        return this;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public CtgMqConfig setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
        return this;
    }

    public String getAuthId() {
        return authId;
    }

    public CtgMqConfig setAuthId(String authId) {
        this.authId = authId;
        return this;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public CtgMqConfig setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
        return this;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public CtgMqConfig setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public CtgMqConfig setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public int getDefaultTopicQueueNums() {
        return defaultTopicQueueNums;
    }

    public CtgMqConfig setDefaultTopicQueueNums(int defaultTopicQueueNums) {
        this.defaultTopicQueueNums = defaultTopicQueueNums;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public CtgMqConfig setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public int getProducerNum() {
        return producerNum;
    }

    public CtgMqConfig setProducerNum(int producerNum) {
        this.producerNum = producerNum;
        return this;
    }
}
