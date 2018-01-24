package com.duangframework.rule.plugin;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.rule.core.RuleFactory;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RulePlugin implements IPlugin {

    private String ruleDir = "/rule";

    public RulePlugin() {

    }

    public RulePlugin(String ruleDir) {
        this.ruleDir = ruleDir;
    }


    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {
        RuleFactory.init(ruleDir);
    }

    @Override
    public void stop() throws Exception {

    }
}
