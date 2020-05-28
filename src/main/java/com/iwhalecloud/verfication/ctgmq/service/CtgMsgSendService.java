package com.iwhalecloud.verfication.ctgmq.service;

import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.exception.MQProducerException;
import com.ctg.mq.api.impl.MQProducerImpl;
import com.iwhalecloud.verfication.mq.aop.MsgSend;
import com.iwhalecloud.verfication.mq.config.CtgMqConfig;
import com.iwhalecloud.verfication.mq.configuration.CtgMqProducerConfiguration;
import com.iwhalecloud.verfication.utils.MsgCreateUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-28 23:45
 **/
@Service
public class CtgMsgSendService implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(CtgMqProducerConfiguration.class);
    @Autowired
    private CtgMqConfig ctgMqConfig;

    private String msg;

    private final List<IMQProducer> imqProducers = new ArrayList<>();

    private final AtomicLong count = new AtomicLong(0);

    @Override
    public void afterPropertiesSet() throws Exception {
        msg = MsgCreateUtils.randomString(3096);
        // 初始化生产者
        if(logger.isInfoEnabled()){
            logger.info("开始初始化生产者，数量【："+ctgMqConfig.getProducerNum()+"】");
        }
        IntStream.range(0, ctgMqConfig.getProducerNum()).forEach(i -> {
            IMQProducer producer = buildProducer(i);
            imqProducers.add(producer);
        });
        // 初始化生产者
        if(logger.isInfoEnabled()){
            logger.info("生产者初始化完毕");
        }
    }
    @MsgSend
    public void sendMSg(String topicName) {
        long size = count.incrementAndGet() % (long)ctgMqConfig.getProducerNum();
        IMQProducer producer = imqProducers.get((int) size);
        MQMessage message = new MQMessage(topicName, msg.getBytes());
        try {
            producer.send(message);
        } catch (MQProducerException e) {
            logger.error("生产者线程投递消息topic【" + topicName + "】异常：" + e.getMessage(), e);
        }
    }

    private IMQProducer buildProducer(int i) {
        final Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerGroupName, ctgMqConfig.getProducerGroupName()+i);
        properties.setProperty(PropertyKeyConst.NamesrvAddr, ctgMqConfig.getNamesrvAddr());
        properties.setProperty(PropertyKeyConst.NamesrvAuthID, ctgMqConfig.getAuthId());
        properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, ctgMqConfig.getAuthPwd());
        properties.setProperty(PropertyKeyConst.ClusterName, ctgMqConfig.getClusterName());
        properties.setProperty(PropertyKeyConst.TenantID, ctgMqConfig.getTenantId());
        IMQProducer imqProducer = CTGMQFactory.createProducer(properties);
        Field producerField = ReflectionUtils.findField(MQProducerImpl.class, "producer");
        assert producerField != null;
        producerField.setAccessible(true);
        DefaultMQProducer defaultMqProducer = (DefaultMQProducer) ReflectionUtils.getField(producerField, imqProducer);
        assert defaultMqProducer != null;
        defaultMqProducer.setDefaultTopicQueueNums(ctgMqConfig.getDefaultTopicQueueNums());
        try {
            imqProducer.connect();
        } catch (MQException e) {
            logger.error("生产者启动失败：" + e.getMessage(), e);
        }
        return imqProducer;
    }

    @Override
    public void destroy() throws Exception {
        imqProducers.forEach(produce->{
            try {
                produce.close();
            } catch (MQException e) {
                logger.error("生产者关闭失败---"+e.getMessage(),e);
            }
        });
    }
}
