package com.duangframework.rpc.common;

/**
 * 消息持有者
 * 用于RPC模块之间数据双向传输
 *
 *                    MessageHolder
 * 生产者 <-------------------------> 消费者之间的传输载体
 *
 *
 * 模仿http协议头定义
 * 请求行，消息报头，请求正文

 Magic //数据包验证位，short类型
 Sign   //消息标志，请求/响应  byte类型
 Status     //响应状态
 ContentLength        // 协议长度
 Body       // 协议内容


 -----------------------------------------------------------
   Magic  |  Sign  |  Status  |  ContentLength  |  Body
 -----------------------------------------------------------
 *
 * @author Created by laotang
 * @date on 2017/12/7.
 */
public class MessageHolder<T> implements java.io.Serializable {


    // 消息标志
    private byte sign;
    // 响应状态
    private byte status;
    // 协议长度
    private int  contentLength;
    // 内容
    private T body;

    public MessageHolder() {
    }

    public MessageHolder(byte sign, byte status, int contentLength, T body) {
        this.sign = sign;
        this.status = status;
        this.contentLength = contentLength;
        this.body = body;
    }

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
