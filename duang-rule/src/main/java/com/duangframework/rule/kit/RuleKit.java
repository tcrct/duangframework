package com.duangframework.rule.kit;

import com.duangframework.rule.core.RuleFactory;
import com.duangframework.rule.entity.RuleParam;
import com.duangframework.rule.entity.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleKit {

    private static Logger logger = LoggerFactory.getLogger(RuleKit.class);
    private static RuleKit _ruleKit = null;
    private static Lock _ruleKitLock = new ReentrantLock();
    private static List<RuleParam> _ruleParamList = null;
    private static RuleParam ruleParam = null;


    public static RuleKit duang() {
        if(null == _ruleKit) {
            try {
                _ruleKitLock.lock();
                _ruleKit = new RuleKit();
                _ruleParamList = new java.util.ArrayList<>();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _ruleKitLock.unlock();
            }
        }
        clear();
        return _ruleKit;
    }

    private static void clear() {
        _ruleParamList.clear();
        ruleParam = new RuleParam();
    }

    public RuleKit name(String ruleName) {
        ruleParam.setRuleName(ruleName);
        return this;
    }
    public RuleKit key(String key) {
        ruleParam.setKey(key);
        return this;
    }
    public RuleKit vlaue(Object value) {
        ruleParam.setValue(value);
        return this;
    }
    /**
     * 要验证的规则名称集合
     * @param ruleParamList
     * @return
     */
    public RuleKit params(List<RuleParam<?>> ruleParamList) {
        _ruleParamList.addAll(ruleParamList);
        return this;
    }

    public RuleKit param(RuleParam<?> ruleParam) {
        _ruleParamList.add(ruleParam);
        return this;
    }

    public RuleResult result() {
        return RuleFactory.execute(_ruleParamList);
    }



}
