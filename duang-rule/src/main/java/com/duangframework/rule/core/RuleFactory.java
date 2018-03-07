package com.duangframework.rule.core;

import com.duangframework.core.annotation.rule.Rule;
import com.duangframework.core.annotation.rule.RuleAction;
import com.duangframework.core.common.classes.CustomizeClassTemplate;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rule.entity.RuleParam;
import com.duangframework.rule.entity.RuleResult;
import com.duangframework.rule.entity.generate.DrlModel;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleFactory {

    private static Logger logger = LoggerFactory.getLogger(RuleFactory.class);
    private static KieSessionHolder kieSessionHolder = null;
    public static String RULE_DIR = "/rules";
    private static boolean isRunPlugin = false;
    private static String ruleActionPackagePath = "";
    /**
     * 是启有启动插件
     *
     */
    public static void start() {
        String basePackagePath = ConfigKit.duang().key("base.package.path").asString();
        CustomizeClassTemplate template = new CustomizeClassTemplate(Rule.class, basePackagePath, "", "");
        try {
            List<Class<?>> classList = template.getList();
            if (ToolsKit.isNotEmpty(classList)) {
                for(Class<?> cls : classList) {
                    if(cls.isAnnotationPresent(Rule.class)) {
                        ruleActionPackagePath = "import static " + cls.getName();
                        Method[] methods = cls.getMethods();
                        if(ToolsKit.isNotEmpty(methods)) {
                            for(Method method : methods) {
                                if(method.isAnnotationPresent(RuleAction.class)){
                                    ruleActionPackagePath += "."+method.getName()+";";
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        isRunPlugin = true;
    }


    /**
     * 初始化 kie 容器对象
     * @param drlModel   drl文件对象
     */
    public static void init(final DrlModel drlModel) {
        init(RULE_DIR, drlModel);
    }

    /**
     * 初始化 kie 容器对象
     * @param ruleDir       规则文件目录
     * @param drlModel   drl文件对象
     */
    public static void init(final String ruleDir, final DrlModel drlModel) {
        // 如果没运行插件则抛出异常
        if(!isRunPlugin) {
            throw  new RuntimeException("RulePlugin is not start!");
        }
        if(ToolsKit.isEmpty(ruleActionPackagePath)) {
            throw  new EmptyNullException("规则回调方法没有设置，请设置@Rule @RuleAction");
        }
        if(ToolsKit.isNotEmpty(drlModel)) {
            List<String> importList = drlModel.getImportPackageList();
            if(ToolsKit.isEmpty(importList)) {
                importList= new ArrayList<>();
            }
            importList.add(0,ruleActionPackagePath);
            drlModel.setImportPackageList(importList);
        }
        if(ToolsKit.isEmpty(kieSessionHolder)) {
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    kieSessionHolder = new KieSessionHolder.Builder().ruleDir(ruleDir).model(drlModel).builder();
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
                if (ruleFiredCount <=  0) {
                    throw new ServiceException().setMessage("verification [" + ruleParam.getRuleName() + "] is not pass!");
                }
            }
        } catch (ServiceException e) {
            ruleResult.setCode(500);
            ruleResult.setMessage(e.getMessage());
            logger.warn(e.getMessage(), e);
        }
        kieSession.destroy();
        return ruleResult;
    }

    public static boolean  reload() {
        return reload(null);
    }

    public static boolean  reload(DrlModel drlModel) {
        return kieSessionHolder.reload(drlModel);
    }

}
