package com.iwhalecloud.verfication.mq.thread;

import com.iwhalecloud.verfication.mq.producer.ProducerMsgSendService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 22:44
 **/
public class ProducerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProducerThread.class);
    private String threadName;
    private String topicName;
    //private String msg;
    private ProducerMsgSendService msgSendService;
    private AbstractQueue<String> blockingQueue;

    public ProducerThread(String threadName, String topicName, ProducerMsgSendService msgSendService, AbstractQueue<String> blockingQueue) {
        this.threadName = threadName;
        this.topicName = topicName;
        this.msgSendService = msgSendService;
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        while (true){
            try {
                String msg = blockingQueue.poll();
                if (logger.isDebugEnabled()) {
                    logger.debug("主题【{}】的线程【{}】开始投递消息【{}】", topicName, threadName, msg);
                }
                if(StringUtils.isNotEmpty(msg)){
                    msgSendService.send(topicName, msg);
                }
                //Thread.sleep(10L);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                logger.error("生产者线程出现中断异常：" + e.getMessage(), e);
            }
        }

    }
}
