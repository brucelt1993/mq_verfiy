package com.iwhalecloud.verfication.controller;

import com.iwhalecloud.verfication.ctgmq.service.CtgMsgSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-29 0:06
 **/
@RestController
@RequestMapping("/ctgMq")
public class CtgMsgSendController {
    @Autowired
    private CtgMsgSendService sendService;
    @Value("${ctgmq.topic.names}")
    private String topicName;

    @RequestMapping("/send")
    public void send() {
        sendService.sendMSg(topicName);
    }
}
