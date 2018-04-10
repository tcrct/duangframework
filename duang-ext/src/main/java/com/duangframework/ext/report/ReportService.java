package com.duangframework.ext.report;

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
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("compute", ComputerInfo.getInstance());
        BootStrap bootStrap = BootStrap.getInstants();
        if(ToolsKit.isNotEmpty(bootStrap)) {
            infoMap.put("host", bootStrap.getHost());
            infoMap.put("prot", bootStrap.getPort());
        }
        return infoMap;
    }

}
