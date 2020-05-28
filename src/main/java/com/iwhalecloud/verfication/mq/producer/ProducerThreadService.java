package com.iwhalecloud.verfication.mq.producer;

import com.ctg.mq.api.IMQProducer;
import com.iwhalecloud.verfication.mq.config.CtgMqThreadConfig;
import com.iwhalecloud.verfication.mq.thread.ProducerThread;
import com.iwhalecloud.verfication.mq.thread.ProducerThread2;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 22:41
 **/
@Order(1)
@Service
public class ProducerThreadService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ProducerThreadService.class);
    @Autowired
    private CtgMqThreadConfig threadConfig;
    @Value("${ctgmq.topic.names}")
    private String topicNames;
    @Autowired
    private ProducerMsgSendService msgSendService;
    @Autowired
    private IMQProducer imqProducer;
    private final ConcurrentHashMap<String, AbstractQueue<String>> msgMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ProducerThreadPool> threadPoolMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
            initQueue(topicName);
            initThreadPool(topicName);
        });
    }

    private void initThreadPool(String topicName) {
        final BlockingQueue blockingQueue = new ArrayBlockingQueue(threadConfig.getQueueSize());
        ProducerThreadPool producerThreadPool = new ProducerThreadPool(threadConfig.getCorePoolSize(), threadConfig.getMaximumPoolSize()
                , 1, TimeUnit.MINUTES, blockingQueue, Executors.defaultThreadFactory(), "pool-" + topicName);
        threadPoolMap.put(topicName, producerThreadPool);
    }

//    public void sendMsg(String topicName,String msg){
//        ProducerThreadPool threadPool = threadPoolMap.get(topicName);
//        ProducerThread producerThread = new ProducerThread("pool_"+topicName,topicName,msg,msgSendService);
//        threadPool.execute(producerThread);
//    }

    private void initQueue(String topicName) {
        final AbstractQueue<String> blockingQueue = new ConcurrentLinkedQueue<String>();
        msgMap.put(topicName, blockingQueue);
    }

    public ConcurrentHashMap getBlockingQueueMap() {
        if (MapUtils.isNotEmpty(msgMap)) {
            return msgMap;
        }
        return null;
    }

//    public void start() {
//        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
//            if (logger.isDebugEnabled()) {
//                logger.debug("开始启动主题【" + topicName + "】的生产者线程池");
//            }
//            ProducerThreadPool threadPool = threadPoolMap.get(topicName);
//            AbstractQueue<String> queue = msgMap.get(topicName);
//            IntStream.range(0, threadConfig.getCorePoolSize()).forEach(i -> {
//                final String threadName = topicName + i;
//                ProducerThread thread = new ProducerThread(threadName, topicName, msgSendService, queue);
//                threadPool.execute(thread);
//            });
//        });
//    }

    public void start2() {
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
            if (logger.isInfoEnabled()) {
                logger.info("开始启动主题【" + topicName + "】的生产者线程池,线程数为："+threadConfig.getCorePoolSize());
            }
            ProducerThreadPool threadPool = threadPoolMap.get(topicName);
            IntStream.range(0, threadConfig.getCorePoolSize()).forEach(i -> {
                ProducerThread2 thread = new ProducerThread2(topicName,msgSendService);
                threadPool.execute(thread);
            });
        });
    }
    //@Scheduled(cron = " 0/30 * * * * ? ")
    public void stop(){
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
            ProducerThreadPool threadPool = threadPoolMap.get(topicName);
            if(logger.isInfoEnabled()){
                logger.info("开始关闭主题【"+topicName+"】的线程池");
            }
            threadPool.shutdown();
        });
    }
}
