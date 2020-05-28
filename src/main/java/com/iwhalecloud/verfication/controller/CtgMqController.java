package com.iwhalecloud.verfication.controller;

import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.exception.MQProducerException;
import com.iwhalecloud.verfication.mq.aop.MsgSendCalculateService;
import com.iwhalecloud.verfication.mq.config.CtgMqThreadConfig;
import com.iwhalecloud.verfication.mq.producer.ProducerMsgSendService;
import com.iwhalecloud.verfication.mq.producer.ProducerThreadService;
import com.iwhalecloud.verfication.mq.task.ThreadPoolStopTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 15:06
 **/
@RestController
@RequestMapping("/mq")
public class CtgMqController {
    private static final Logger logger = LoggerFactory.getLogger(CtgMqController.class);
    @Autowired
    private IMQProducer imqProducer;
    @Autowired
    private ProducerThreadService threadService;
    @Value("${ctgmq.topic.names}")
    private String topicNames;
    @Autowired
    private MsgSendCalculateService msgSendCalculateService;
    @Autowired
    private ProducerMsgSendService producerMsgSendService;
    @Autowired
    private ThreadPoolStopTask stopTask;
    @Autowired
    private CtgMqThreadConfig threadConfig;

    @RequestMapping("/test")
    public String test(String topic, String msg) {
        String result;
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        String key = topic + threadLocalRandom.nextInt(99999);
        MQMessage message = new MQMessage(topic, key, "ORDER_TAG", msg.getBytes());
        try {
            imqProducer.send(message);
        } catch (MQProducerException e) {
            logger.error("发送消息失败：" + e.getMessage(), e);
            result = "topic【" + topic + "】发送消息【" + msg + "】失败";
        }
        result = "topic【" + topic + "】发送消息【" + msg + "】成功";
        return result;
    }
    @RequestMapping("/send")
    public String send(String msg) {
        String result;
        ConcurrentHashMap<String, AbstractQueue<String>>  map = threadService.getBlockingQueueMap();
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
            AbstractQueue<String> blockingQueue = map.get(topicName);
            blockingQueue.add(msg);
        });
        return "success";
    }

    @RequestMapping("/send2")
    public String send2(String msg) {
        String result;
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
           producerMsgSendService.send(topicName,msg);
        });
        return "success";
    }
    @RequestMapping("/start")
    public String start(){
        threadService.start2();
        Timer timer = new Timer();
        timer.schedule(stopTask,threadConfig.getExecuteTime()*1000);
        return "success";
    }

    @RequestMapping("/clear")
    public void clear(){
        msgSendCalculateService.clear();
    }
}
