package com.duangframework.ext.report;

import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.common.Const;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.InstanceFactory;
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

    public Map<String, List<Action>> treeActions() {
        List<String> keyList = getAllActionKeys();
        Map<String, List<Action>> treeMap = new TreeMap<>();
        for (String key : keyList) {
            if(key.contains(Const.REPORT_MAPPING_KEY)) {
                continue;
            }
            Action action = getActionMapping().get(key);
            String controllerKey = action.getControllerKey();
            if(treeMap.containsKey(controllerKey)) {
                treeMap.get(controllerKey).add(action);
            } else {
                List<Action> itemList = new ArrayList<>();
                itemList.add(action);
                treeMap.put(controllerKey, itemList);
            }
        }
        return treeMap;
    }

}
