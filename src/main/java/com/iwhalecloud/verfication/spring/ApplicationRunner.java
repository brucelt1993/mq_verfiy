package com.iwhalecloud.verfication.spring;

import com.ctg.mq.api.IMQProducer;
import com.iwhalecloud.verfication.mq.producer.ProducerThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 20:52
 **/
@Configuration
public class ApplicationRunner implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ProducerThreadService threadService;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始启动生产者线程池组~~~~~");
        //threadService.start();
    }
}
