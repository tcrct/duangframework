package com.duangframework.rpc.common;

/**
 * @author Created by laotang
 * @date on 2017/12/7.
 */
public class Protocol {
    /** 协议头长度 */
    public static final int HEADER_LENGTH = 9;
    /** Magic */
    public static final short MAGIC = (short) 0xabc;


    public static final byte REQUEST    = 0x01;    // 1 请求  Client --> Server
    public static final byte RESPONSE  = 0x02;    // 2 响应  Server --> Client


    public static final byte OK  = (byte) 200;    //  正常响应

}
