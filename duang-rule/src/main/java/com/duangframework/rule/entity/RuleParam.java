package com.duangframework.rule.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleParam {

    /**
     * 规则名称 <br/>
     * drools规则表drl里的 rule 字段值
     *
     */
    private String ruleName;
    /**
     * 需要验证的字段名与缓存值对象
     */
    private List<ParamItem<?>> paramItemList;

    public RuleParam() {
    }

    public RuleParam(String ruleName, ParamItem<?> paramItem) {
        this.ruleName = ruleName;
        this.paramItemList = new ArrayList<>(1);
        this.paramItemList.add(paramItem);
    }

    public RuleParam(String ruleName, List<ParamItem<?>> paramItemList) {
        this.ruleName = ruleName;
        this.paramItemList = paramItemList;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<ParamItem<?>> getParamItemList() {
        return paramItemList;
    }

    public void setParamItemList(List<ParamItem<?>> paramItemList) {
        this.paramItemList = paramItemList;
    }

    public Map<String, Object> toMap() {
        if(null != paramItemList && !paramItemList.isEmpty()) {
            Map<String, Object> map = new java.util.TreeMap<>();
            for(ParamItem paramItem : paramItemList) {
                map.put(paramItem.getKey(), paramItem.getValue());
            }
            return map;
        }
        return null;
    }
}
