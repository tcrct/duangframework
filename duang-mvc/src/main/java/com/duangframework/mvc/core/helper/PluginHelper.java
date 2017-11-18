package com.duangframework.mvc.core.helper;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 *  Plugin帮助类
 *
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class PluginHelper {

    private static Logger logger = LoggerFactory.getLogger(PluginHelper.class);

    public static void duang() {
       start();
    }

    public static void start() {
        for(Iterator<IPlugin> it = InstanceFactory.getPlugins().iterator(); it.hasNext();){
            IPlugin plugin = it.next();
            if(null != plugin){
                try{
                    plugin.start();
                    logger.warn(plugin.getClass().getName() +" start success...");
                }catch(Exception ex){
                    logger.warn(plugin.getClass().getName() +" start fail: " + ex.getMessage());
                }
            }
        }
    }

    public static void stop(){
        List<IPlugin> plugins = InstanceFactory.getPlugins();
        if(null != plugins && !plugins.isEmpty()){
            for(int i=plugins.size()-1; i>=0; i--){
                try {
                    plugins.get(i).stop();
                    logger.warn(plugins.get(i).getClass().getName() +" stop success...");
                }catch (Exception ex) {
                    logger.warn(plugins.get(i).getClass().getName() + " stop fail: " + ex.getMessage() );
                }
            }
        }
    }
}
