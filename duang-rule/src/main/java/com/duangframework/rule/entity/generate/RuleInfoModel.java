package com.duangframework.rule.entity.generate;

import com.duangframework.rule.entity.ParamItem;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class RuleInfoModel {
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则说明
     */
    private String ruleDesc;
    /**
     * 是否循环调用
     */
    private boolean noLoop = true;
    /**
     *优先级，1为最高，如果规则名称后缀一致时，先根据这个值进行验证规则
     */
    private int salience = 1;
    /**
     *  验证规则集合
     */
    private List<ParamItem> whenList;
    /**
     * 验证规则通过后的执行命令
     */
    private String then;

    public RuleInfoModel() {
    }

    public RuleInfoModel(String ruleName, String ruleDesc, boolean noLoop, int salience, List<ParamItem> whenList, String then) {
        this.ruleName = ruleName;
        this.ruleDesc = ruleDesc;
        this.noLoop = noLoop;
        this.salience = salience;
        this.whenList = whenList;
        this.then = then;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public boolean isNoLoop() {
        return noLoop;
    }

    public void setNoLoop(boolean noLoop) {
        this.noLoop = noLoop;
    }

    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public List<ParamItem> getWhenList() {
        return whenList;
    }

    public void setWhenList(List<ParamItem> whenList) {
        this.whenList = whenList;
    }

    public String getThen() {
        return then;
    }

    public void setThen(String then) {
        this.then = then;
    }
}
