package com.iwhalecloud.verfication.mq.aop;

import com.iwhalecloud.verfication.mq.calculate.MsgSendCalculateDto;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-26 14:59
 **/
@Service
public class MsgSendCalculateService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MsgSendCalculateService.class);
    private final ConcurrentHashMap<String, Queue<Long>> msgSendMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Integer> tpsQueue = new ConcurrentLinkedDeque<>();

    @Value("${ctgmq.topic.names}")
    private String topicNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tpsQueue.add(0);
        Arrays.stream(StringUtils.split(topicNames, ",")).forEach(topicName -> {
            Queue<Long> timeQueue = new ConcurrentLinkedDeque<>();
            msgSendMap.put(topicName, timeQueue);
        });
    }

    public void clear() {
        if (MapUtils.isNotEmpty(msgSendMap)) {
            msgSendMap.values().forEach(Collection::clear);
        }
    }

    public void add(String topicName, Long sendTime) {
        Queue<Long> queue = msgSendMap.get(topicName);
        if (queue != null) {
            queue.add(sendTime);
        }
    }

    @Scheduled(cron = " 0/20 * * * * ? ")
    public void calculate() {
        msgSendMap.forEach((topicName,queue)->{
            MsgSendCalculateDto calculateDto = new MsgSendCalculateDto();
            calculateDto.setTopicName(topicName);
            List<Long> costList = new ArrayList<>(queue);
            int size = costList.size();
            if(size<=0){
                if(logger.isInfoEnabled()){
                    logger.info("主题【"+topicName+"】未生产数据~~~~");
                }
                return;
            }
            int lastSize = tpsQueue.poll();
            int diff = size - lastSize;
            calculateDto.setTps((long) (diff/20));
            tpsQueue.add(size);
            Collections.sort(costList);
            if (size % 2 == 0) {
                Long midValue = (costList.get(size / 2 - 1) + costList.get(size / 2)) / 2;
                calculateDto.setMidValue(midValue);
            } else {
                Long midValue = costList.get((size + 1) / 2 - 1);
                calculateDto.setMidValue(midValue);
            }
            calculateDto.setMinValue(costList.get(0));
            calculateDto.setMaxValue(costList.get(size - 1));
            calculateDto.setCount((long) size);
            calculateDto.setPercentage50(getpPercentValue(50, costList));
            calculateDto.setPercentage75(getpPercentValue(75, costList));
            calculateDto.setPercentage90(getpPercentValue(90, costList));
            calculateDto.setPercentage99(getpPercentValue(99, costList));
            if (logger.isInfoEnabled()) {
                logger.info(calculateDto.toString());
            }
        });
    }

    private Long getpPercentValue(int percent, List<Long> list) {
        int size = list.size();
        double percentDouble = percent * 0.01;
        double percentIndex = Math.floor(size * percentDouble);
        int index = (int) percentIndex;
        if (index < 0) {
            index = 0;
        } else if (index > size - 1) {
            index = size - 1;
        }
        return list.get(index);
    }
}
