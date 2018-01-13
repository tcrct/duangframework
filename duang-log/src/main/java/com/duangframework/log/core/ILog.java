package com.duangframework.log.core;

/**
 *  日志接口
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public interface ILog<T> {

    void submit(T requestDto) throws Exception;
}
