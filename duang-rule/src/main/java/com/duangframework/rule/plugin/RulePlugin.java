package com.duangframework.rule.plugin;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.rule.core.RuleFactory;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RulePlugin implements IPlugin {



    public RulePlugin() {

    }

    public RulePlugin(String ruleDir) {
        RuleFactory.RULE_DIR = ruleDir;
    }


    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {
        RuleFactory.start();
    }

    @Override
    public void stop() throws Exception {

    }
}
