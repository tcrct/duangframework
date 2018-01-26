package com.duangframework.rule.kit;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rule.core.RuleFactory;
import com.duangframework.rule.entity.ParamItem;
import com.duangframework.rule.entity.RuleParam;
import com.duangframework.rule.entity.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    /**
     * 规则名称
     * @param ruleName
     * @return
     */
    public RuleKit name(String ruleName) {
        ruleParam.setRuleName(ruleName);
        return this;
    }

    /**
     *  要验证规则的字段及值对象
     * @param paramItem
     * @return
     */
    public RuleKit param(ParamItem<?> paramItem) {
        List<ParamItem<?>> paramItemList = new ArrayList<>(1);
        paramItemList.add(paramItem);
        ruleParam.setParamItemList(paramItemList);
        return this;
    }
    /**
     * 要验证的规则名称集合
     * @param ruleParamList
     * @return
     */
    public RuleKit params(List<RuleParam> ruleParamList) {
        _ruleParamList.addAll(ruleParamList);
        return this;
    }

    /**
     * 要验证的规则
     * @param ruleParam
     * @return
     */
    public RuleKit param(RuleParam ruleParam) {
        _ruleParamList.add(ruleParam);
        return this;
    }

    /**
     * 重载DRL规则文件
     * @return
     */
    public boolean reload() {
        return RuleFactory.reload();
    }

    /**
     * 返回执行结果
     * @return
     */
    public RuleResult result() {
        if(ToolsKit.isNotEmpty(ruleParam.getRuleName()) && ToolsKit.isNotEmpty(ruleParam.getParamItemList())) {
            _ruleParamList.add(ruleParam);
        }
        return RuleFactory.execute(_ruleParamList);
    }
}
