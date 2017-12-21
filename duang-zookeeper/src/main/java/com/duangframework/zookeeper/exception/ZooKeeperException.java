package com.duangframework.zookeeper.exception;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ZooKeeperException extends RuntimeException {

    public ZooKeeperException() {
        super();
    }

    public ZooKeeperException(String msg) {
        super(msg);
    }

    public ZooKeeperException(String msg , Throwable cause) {
        super(msg, cause);
    }

}