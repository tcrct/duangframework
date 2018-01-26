package com.duangframework.rule.core;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rule.entity.RuleParam;
import com.duangframework.rule.entity.RuleResult;
import com.duangframework.rule.entity.generate.DrlModel;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleFactory {

    private static Logger logger = LoggerFactory.getLogger(RuleFactory.class);
    private static KieSessionHolder kieSessionHolder = null;

    /**
     * 初始化 kie 容器对象
     * @param ruleDir       规则文件目录
     */
    public static void init(final String ruleDir) {
        init(ruleDir, null);
    }

    /**
     * 初始化 kie 容器对象
     * @param ruleDir       规则文件目录
     * @param drlModel   drl文件对象
     */
    public static void init(final String ruleDir, final DrlModel drlModel) {
        if(ToolsKit.isEmpty(kieSessionHolder)) {
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    kieSessionHolder = new KieSessionHolder.Builder().ruleDir(ruleDir).builder();
                }
            });
        }
    }

    /**
     * 执行规则验证
     * @param ruleParams
     * @return
     */
    public static RuleResult  execute(List<RuleParam> ruleParams) {
        if(ToolsKit.isEmpty(ruleParams)) {
            throw new EmptyNullException("ruleParams is null");
        }
        if(ToolsKit.isEmpty(kieSessionHolder)) {
            throw new EmptyNullException("RuleFactory.kieSessionHolder is null");
        }
        KieSession kieSession = kieSessionHolder.kieSession();
        RuleResult ruleResult = new RuleResult(200, "success");
        try {
            for (RuleParam ruleParam : ruleParams) {
                Map<String, Object> ruleParamsMap = ruleParam.toMap();
                kieSession.insert(ruleParamsMap);
                int ruleFiredCount = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter(ruleParam.getRuleName()));
                if (ruleFiredCount <  0) {
                    throw new ServiceException("验证[" + ruleParam.getRuleName() + "]不通过");
                }
            }
        } catch (Exception e) {
            ruleResult.setCode(500);
            ruleResult.setMessage(e.getMessage());
            logger.warn(e.getMessage(), e);
        }
        kieSession.destroy();
        return ruleResult;
    }

    public static boolean  reload() {
        return kieSessionHolder.reload();
    }

}
