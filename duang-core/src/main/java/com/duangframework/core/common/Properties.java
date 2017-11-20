package com.duangframework.core.common;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class Properties {

    private static Configuration configuration = null;
    public static final Configuration getConfiguration() {
        try {
            if(null == configuration){
                configuration = new PropertiesConfiguration("duang.properties");
                //如果是布署到正式生产环境下的，则通过URL读取对应的配置文件
                if(ToolsKit.isNotEmpty(configuration) && ToolsKit.isAliyunHost()) {
                    String productCode = configuration.getString("product.code");
                    String url = configuration.getString("properties.url");
                    if(ToolsKit.isEmpty(productCode)) { throw new EmptyNullException("duang.properties[product.code] is null !!!"); }
                    configuration.clear();
                    String urlString  = url + ToolsKit.getUseEnv().toLowerCase() + "/";
                    configuration =getConfigurationByUrl(urlString, productCode+".properties");
                }
            }
            return configuration;
        } catch (Exception e) {
            try{
                configuration = new PropertiesConfiguration("duangbase.properties");
                return configuration;
            } catch(Exception ex){
                throw new EmptyNullException("Cannot find duang.properties !!!");
            }
        }
    }

    /**
     * 根据配置文件名取网络上的配置文件并返回Configuration对象
     * @param propertiesName
     * @return
     * @throws Exception
     */
    private static Configuration getConfigurationByUrl(String urlStr, String propertiesName) throws Exception {
        String urlString = urlStr + propertiesName;
        URL url = new URL(urlString);
        if(ToolsKit.isEmpty(url)) {
            throw new EmptyNullException("get properties fail:  "  + urlString);
        }
        return new PropertiesConfiguration(url);
    }
}
