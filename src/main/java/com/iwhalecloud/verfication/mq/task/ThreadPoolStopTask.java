package com.iwhalecloud.verfication.mq.task;

import com.iwhalecloud.verfication.mq.producer.ProducerMsgSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-27 21:10
 **/
@Component
public class ThreadPoolStopTask extends TimerTask {
    @Autowired
    private ProducerMsgSendService sendService;
    @Override
    public void run() {
        sendService.stop();
    }
}
