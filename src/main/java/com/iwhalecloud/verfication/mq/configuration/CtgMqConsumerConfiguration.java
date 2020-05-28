package com.iwhalecloud.verfication.mq.configuration;

import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQPushConsumer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.enums.MQConsumeFromWhere;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.listener.ConsumerTopicStatus;
import com.iwhalecloud.verfication.mq.config.CtgMqConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luotuan
 * @Description  ctg-mq 消费者配置
 * @create 2020-05-25 11:42
 **/
@SpringBootConfiguration
public class CtgMqConsumerConfiguration implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(CtgMqConsumerConfiguration.class);
    @Value("${ctgmq.topic.names}")
    private String topicNames;
    private final CtgMqConfig ctgMqConfig;
    private ConcurrentHashMap<String, IMQPushConsumer> consumersMap = new ConcurrentHashMap<>();
    @Autowired
    public CtgMqConsumerConfiguration(CtgMqConfig ctgMqConfig) {
        this.ctgMqConfig = ctgMqConfig;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Arrays.stream(StringUtils.split(topicNames,",")).forEach(topicName->{
            ComsumerInitThread initThread = new ComsumerInitThread(topicName);
            new Thread(initThread).start();
        });
    }

    @Override
    public void destroy() throws Exception {
        if (consumersMap != null) {
            logger.error("关闭消费者实例");
            consumersMap.values().forEach(consumer -> {
                try {
                    consumer.close();
                } catch (MQException e) {
                    logger.error("消费者关闭失败：" + e.getMessage(), e);
                }
            });
        }
    }
    class ComsumerInitThread implements Runnable{
        private String topicName;

        public ComsumerInitThread(String topicName) {
            this.topicName = topicName;
        }

        @Override
        public void run() {
            final Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.ConsumerGroupName, "consumer_" + topicName);
            properties.setProperty(PropertyKeyConst.NamesrvAddr, ctgMqConfig.getNamesrvAddr());
            properties.setProperty(PropertyKeyConst.NamesrvAuthID, ctgMqConfig.getAuthId());
            properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, ctgMqConfig.getAuthPwd());
            properties.setProperty(PropertyKeyConst.ClusterName, ctgMqConfig.getClusterName());
            properties.setProperty(PropertyKeyConst.TenantID, ctgMqConfig.getTenantId());
            properties.setProperty(PropertyKeyConst.ConsumeFromWhere, MQConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET.name());
            IMQPushConsumer imqConsumer = CTGMQFactory.createPushConsumer(properties);
            try {
                imqConsumer.connect();
                if(logger.isInfoEnabled()){
                    logger.info("主题【"+topicName+"】初始化消费者");
                }
                imqConsumer.listenTopic(topicName, null, list -> {
                    //开始消费消息
                    if (logger.isDebugEnabled()) {
                        logger.debug("开始消费主题【" + topicName + "】的消息，消息数量【" + list.size() + "】");
                    }
                    return ConsumerTopicStatus.CONSUME_SUCCESS;//对消息批量确认(成功)
                });
                consumersMap.put(topicName, imqConsumer);
            } catch (MQException e) {
                logger.error("消费者初始化失败：" + e.getMessage(), e);
            }
        }
    }
}
