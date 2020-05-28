package com.iwhalecloud.verfication.mq.configuration;

import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.impl.MQProducerImpl;
import com.iwhalecloud.verfication.mq.config.CtgMqConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author luotuan
 * @Description ctgMQ 生产者配置
 * @create 2020-05-25 10:27
 **/
@SpringBootConfiguration
@Order(0)
public class CtgMqProducerConfiguration implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(CtgMqProducerConfiguration.class);
    @Autowired
    private CtgMqConfig ctgMqConfig;

    private IMQProducer imqProducer;

    @Bean
    public IMQProducer getProducer() {
        final Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerGroupName, ctgMqConfig.getProducerGroupName());
        properties.setProperty(PropertyKeyConst.NamesrvAddr, ctgMqConfig.getNamesrvAddr());
        properties.setProperty(PropertyKeyConst.NamesrvAuthID, ctgMqConfig.getAuthId());
        properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, ctgMqConfig.getAuthPwd());
        properties.setProperty(PropertyKeyConst.ClusterName, ctgMqConfig.getClusterName());
        properties.setProperty(PropertyKeyConst.TenantID, ctgMqConfig.getTenantId());
        imqProducer = CTGMQFactory.createProducer(properties);
        Field producerField = ReflectionUtils.findField(MQProducerImpl.class, "producer");
        assert producerField != null;
        producerField.setAccessible(true);
        DefaultMQProducer defaultMqProducer = (DefaultMQProducer) ReflectionUtils.getField(producerField,imqProducer);
        assert defaultMqProducer != null;
        defaultMqProducer.setDefaultTopicQueueNums(ctgMqConfig.getDefaultTopicQueueNums());
        try {
            imqProducer.connect();
            if(logger.isInfoEnabled()){
                logger.info("生产者【"+ctgMqConfig.getProducerGroupName()+"】开始初始化");
            }
        } catch (MQException e) {
            logger.error("生产者启动失败："+e.getMessage(),e);
        }
        return imqProducer;
    }
    @Override
    public void destroy() throws Exception {
        if(imqProducer!=null){
            logger.error("开始销毁生产者【"+ctgMqConfig.getProducerGroupName()+"】");
            imqProducer.close();
        }
    }
}
