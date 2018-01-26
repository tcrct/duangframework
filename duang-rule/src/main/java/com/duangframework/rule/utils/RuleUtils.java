package com.duangframework.rule.utils;

import com.duangframework.rule.entity.generate.DrlModel;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class RuleUtils {

    /**
     *  创建Drl文件
     * @param drlModel      文件内容对象
     * @return  文件内容字符串
     * @throws Exception
     */
    public static String createDrlFile(DrlModel drlModel) {
        try {
            return AutoCreateDrlFile.builder(drlModel);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
