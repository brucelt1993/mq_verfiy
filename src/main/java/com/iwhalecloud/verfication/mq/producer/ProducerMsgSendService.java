package com.iwhalecloud.verfication.mq.producer;

import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.bean.MQSendResult;
import com.ctg.mq.api.bean.MQSendStatus;
import com.ctg.mq.api.exception.MQProducerException;
import com.iwhalecloud.verfication.mq.aop.MsgSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-26 15:36
 **/
@Service
@Order(1)
public class ProducerMsgSendService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerMsgSendService.class);
    private final IMQProducer imqProducer;
    private volatile boolean threadFlag = true;

    public boolean isThreadFlag() {
        return threadFlag;
    }

    public ProducerMsgSendService setThreadFlag(boolean threadFlag) {
        this.threadFlag = threadFlag;
        return this;
    }

    @Autowired
    public ProducerMsgSendService(IMQProducer imqProducer) {
        this.imqProducer = imqProducer;
    }

    @MsgSend
    public boolean send(String topicName, String msg) {
        boolean result = false;
        String key = String.valueOf(ThreadLocalRandom.current().nextLong(999999999));
        MQMessage message = new MQMessage(topicName, key, "ORDER_TAG", msg.getBytes());
        try {
            MQSendResult mqSendResult = imqProducer.send(message);
            result = mqSendResult.getSendStatus().equals(MQSendStatus.SEND_OK);
        } catch (MQProducerException e) {
            logger.error("生产者线程投递消息topic【" + topicName + "】异常：" + e.getMessage(), e);
        }
        return result;
    }

    public void stop(){
        setThreadFlag(false);
    }
}
