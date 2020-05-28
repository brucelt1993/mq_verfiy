package com.iwhalecloud.verfication.mq.thread;

import com.iwhalecloud.verfication.mq.producer.ProducerMsgSendService;
import com.iwhalecloud.verfication.utils.MsgCreateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-26 17:26
 **/
public class ProducerThread2 implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProducerThread2.class);
    private String topicName;
    private ProducerMsgSendService msgSendService;
    private static String msg;

    static {
        msg = MsgCreateUtils.randomString(3096);
    }

    public ProducerThread2(String topicName, ProducerMsgSendService msgSendService) {
        this.topicName = topicName;
        this.msgSendService = msgSendService;
    }

    @Override
    public void run() {
        while (msgSendService.isThreadFlag()) {
            try {
                msgSendService.send(topicName, msg);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                logger.error("生产者线程出现中断异常：" + e.getMessage(), e);
            }
        }
    }
}

