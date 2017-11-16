package com.duangframework.mvc.core.base;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.kit.ClassScanKit;

import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class BeanHandle {


    public static void duang() {
        //扫描指定包路径下的类文件，类文件包含有指定的注解类或文件名以指定的字符串结尾的
        Map<String, List<Class<?>>> classMap =  ClassScanKit.duang()
                .annotations(InstanceFactory.ANNOTATION_SET)
                .packages("com.syt.qingbean")
                .jarname("qingbean")
                .map();

        // 分别存放
        if(ToolsKit.isNotEmpty(classMap)) {
                InstanceFactory.setAllClassMap(classMap);
//            for(Iterator<Map.Entry<String, List<Class<?>>>> it = classMap.entrySet().iterator(); it.hasNext();) {
//                Map.Entry<String, List<Class<?>>> entry = it.next();
//                String key = entry.getKey();
//                List<Class<?>> value = entry.getValue();
//                if(Controller.class.getSimpleName().equalsIgnoreCase(key)) {
//
//                }
//            }


//            List<Class<?>> controllerClassList = classMap.get(Const.CONTROLLER_ENDWITH_NAME);
//            if(ToolsKit.isNotEmpty(controllerClassList)) {
//                for(Iterator<Class<?>> it = controllerClassList.iterator(); it.hasNext();) {
//                    InstanceFactory.addControllerClass2Map(it.next());
//                }
//            }
//
//            List<Class<?>> serviceClassList = classMap.get(Const.SERVICE_ENDWITH_NAME);
//            if(ToolsKit.isNotEmpty(serviceClassList)) {
//                for(Iterator<Class<?>> it = serviceClassList.iterator(); it.hasNext();) {
//                    InstanceFactory.addServiceClass2Map(it.next());
//                }
//            }
        }
    }

//    public static List<Class<?>> newInstance() {
//
//    }


}
