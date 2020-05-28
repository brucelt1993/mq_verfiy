package com.iwhalecloud.verfication.mq.calculate;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-25 21:50
 **/
public class MsgSendCalculateDto {
    private String topicName;
    private Long minValue;
    private Long maxValue;
    private Long midValue;
    private Long percentage50;
    private Long percentage75;
    private Long percentage90;
    private Long percentage99;
    private Long count;
    private Long tps;

    public String getTopicName() {
        return topicName;
    }

    public MsgSendCalculateDto setTopicName(String topicName) {
        this.topicName = topicName;
        return this;
    }

    public Long getMinValue() {
        return minValue;
    }

    public MsgSendCalculateDto setMinValue(Long minValue) {
        this.minValue = minValue;
        return this;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public MsgSendCalculateDto setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public Long getMidValue() {
        return midValue;
    }

    public MsgSendCalculateDto setMidValue(Long midValue) {
        this.midValue = midValue;
        return this;
    }

    public Long getPercentage50() {
        return percentage50;
    }

    public MsgSendCalculateDto setPercentage50(Long percentage50) {
        this.percentage50 = percentage50;
        return this;
    }

    public Long getPercentage75() {
        return percentage75;
    }

    public MsgSendCalculateDto setPercentage75(Long percentage75) {
        this.percentage75 = percentage75;
        return this;
    }

    public Long getPercentage90() {
        return percentage90;
    }

    public MsgSendCalculateDto setPercentage90(Long percentage90) {
        this.percentage90 = percentage90;
        return this;
    }

    public Long getPercentage99() {
        return percentage99;
    }

    public MsgSendCalculateDto setPercentage99(Long percentage99) {
        this.percentage99 = percentage99;
        return this;
    }

    public Long getCount() {
        return count;
    }

    public MsgSendCalculateDto setCount(Long count) {
        this.count = count;
        return this;
    }

    public Long getTps() {
        return tps;
    }

    public MsgSendCalculateDto setTps(Long tps) {
        this.tps = tps;
        return this;
    }

    @Override
    public String toString() {
        return new StringBuilder("主题")
                .append("【"+topicName+"】的投递消息耗时分布：")
                .append("投递消息数【" + count+"】,")
                .append("tps【" + tps+"】,")
                .append("最小值【" + minValue+"ms】,")
                .append("最大值【" + maxValue+"ms】,")
                .append("中位数【" + midValue+"ms】,")
                .append("50%的耗时在【" + percentage50+"ms】内,")
                .append("75%的耗时在【" + percentage75+"ms】内,")
                .append("90%的耗时在【" + percentage90+"ms】内,")
                .append("99%的耗时在【" + percentage99+"ms】内")
                .toString();
    }

}
