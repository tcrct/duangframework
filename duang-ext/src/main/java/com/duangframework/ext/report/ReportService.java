package com.duangframework.ext.report;

import com.duangframework.core.annotation.mvc.Mapping;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.server.netty.server.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final Map<String, Action> actionMapping = new HashMap<>();


    private Map<String, Action> getActionMapping() {
        if(actionMapping.isEmpty()) {
            actionMapping.putAll(InstanceFactory.getActionMapping());
            actionMapping.putAll(InstanceFactory.getRestfulActionMapping());
        }
        return actionMapping;
    }

    private List<String> getAllActionKeys() {
        List<String> allActionKeys = new ArrayList<>(getActionMapping().keySet());
        Collections.sort(allActionKeys);
        return allActionKeys;
    }

    public Map<String, Action> actions() {
        List<String> keyList = getAllActionKeys();
        Map<String, Action> treeMap = new TreeMap<>();
        for (String key : keyList) {
            if(key.contains(Const.REPORT_MAPPING_KEY)) {
                continue;
            }
            treeMap.put(key, getActionMapping().get(key));
        }
        return treeMap;
    }

    public Map<String, Map> treeActions() {
        List<String> keyList = getAllActionKeys();
        Map<String, Action> treeMap = new TreeMap<>();
        Map<String, List<Action>> treeItemMap = new TreeMap<>();
        for (String key : keyList) {
            if(key.contains(Const.REPORT_MAPPING_KEY)) {
                continue;
            }
            Action action = getActionMapping().get(key);
//            ActionInfoDto infoDto = new ActionInfoDto();
//            ReportUtils.conversionDto(action, infoDto);
            String controllerKey = action.getControllerKey();
            if(treeItemMap.containsKey(controllerKey)) {
                treeItemMap.get(controllerKey).add(action);
            } else {
                List<Action> itemList = new ArrayList<>();
                itemList.add(action);
                treeItemMap.put(controllerKey, itemList);
                controllerKey = action.getControllerAction().getControllerKey();
                if(!treeMap.containsKey(controllerKey)) {
                    treeMap.put(controllerKey, action.getControllerAction());
                }
            }
        }
        Map<String, Map> mapList = new TreeMap();
        mapList.put("controller", treeMap);
        mapList.put("method", treeItemMap);
        return mapList;
    }

    public Map<String, Object> info() {
        Map<String, Object> computerInfoMap = new HashMap<>();
        computerInfoMap.put("compute", ComputerInfo.getInstance());
        BootStrap bootStrap = BootStrap.getInstants();
        if(ToolsKit.isNotEmpty(bootStrap)) {
            computerInfoMap.put("host", bootStrap.getHost());
            computerInfoMap.put("prot", bootStrap.getPort());
            computerInfoMap.put("ssl", bootStrap.isSslEnabled());
        }
        Map<String, Map> treeActions = treeActions();
        if(ToolsKit.isNotEmpty(treeActions)) {
            computerInfoMap.put("controllerCount", ToolsKit.isEmpty(treeActions.get("controller")) ? 0 : treeActions.get("controller").size());
            computerInfoMap.put("methodCount", ToolsKit.isEmpty(getActionMapping()) ? 0 : getActionMapping().size());
        }
        computerInfoMap.put("author", "duangframework");

        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("info", computerInfoMap); //服务器信息
        infoMap.put("api", treeActions);// api接口信息

        return infoMap;
    }


    /**
     * 自动生成api文档
     * 遍历出所有的Action，取出每个method的@Mapping注解，再判断是否存在@Param
     * 如果有则进行内容生成
     *    Param注解分自定义类型参数及基础参数，自定义类型的要结合@Vtor的注解一并使用
     */
    public void autoCreateApiDocument(){
        Map<String, Action> actions = actions();
        if(ToolsKit.isEmpty(actions)) {
            return;
        }
        for (Iterator<Map.Entry<String,Action>> iterator = actions.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Action> entry = iterator.next();
            Action action = entry.getValue();
            if(ToolsKit.isEmpty(action) || ToolsKit.isEmpty(action.getMethod())) {
                continue;
            }
            Mapping mapping = action.getMethod().getAnnotation(Mapping.class);
            if(ToolsKit.isEmpty(mapping)) {
                continue;
            }
//            mapping.


        }

    }
}
