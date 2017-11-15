package com.duangframework.core.common.classes;

import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/11/15.
 */
public interface IClassTemplate {

    List<Class<?>> getList() throws Exception;

    Map<String, List<Class<?>>> getMap() throws Exception;
}
