package com.iwhalecloud.verfication.mq.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-26 14:46
 **/
@Aspect
@Component
public class MsgSendAspect {
    private static final Logger logger = LoggerFactory.getLogger(MsgSendAspect.class);

    @Autowired
    private MsgSendCalculateService msgSendCalculateService;

    @Pointcut("@annotation(MsgSend)")
    public void msgSendTimeAspect() {

    }

    @Around("msgSendTimeAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        LocalDateTime startDateTime = LocalDateTime.now();
        Object result = null;
        Object[] args = joinPoint.getArgs();
        String topicName = (String) args[0];
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("执行切面出现异常", throwable);
        }finally {
            LocalDateTime finishDateTime = LocalDateTime.now();
            Duration duration = Duration.between(startDateTime, finishDateTime);
            msgSendCalculateService.add(topicName,duration.toMillis());
        }
        return result;
    }
}
