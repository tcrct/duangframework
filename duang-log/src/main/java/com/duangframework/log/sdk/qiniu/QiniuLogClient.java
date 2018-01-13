package com.duangframework.log.sdk.qiniu;

import com.duangframework.log.core.ILog;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class QiniuLogClient implements ILog<QiniuLogDto> {

    private static QiniuLogClient ourInstance = new QiniuLogClient();

    public static QiniuLogClient getInstance() {
        return ourInstance;
    }

    private QiniuLogClient() {
    }


    @Override
    public void submit(QiniuLogDto requestDto) {

    }
}
