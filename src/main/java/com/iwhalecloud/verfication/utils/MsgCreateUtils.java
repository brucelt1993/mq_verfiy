package com.iwhalecloud.verfication.utils;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-27 20:33
 **/
public class MsgCreateUtils {
    public static String createDataSize(int msgSize) {
        StringBuilder sb = new StringBuilder(msgSize);
        for (int i=0; i<msgSize; i++) {
            sb.append('a');
        }
        return sb.toString();
    }

    public static String createMSg(){
        return createDataSize(1024);
    }

    public static String randomString(long size) {
        StringBuilder stringBuilder = new StringBuilder();
        while (size-- > 0) {
            stringBuilder.append((char) RandomUtils.nextInt(33, 126));
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        String ss = createDataSize(1024);
        System.out.println(ss.getBytes().length/1024);
    }
}
