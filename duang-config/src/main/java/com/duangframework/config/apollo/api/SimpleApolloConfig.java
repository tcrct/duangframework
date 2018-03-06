package com.duangframework.config.apollo.api;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.duangframework.config.apollo.model.ApolloModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class SimpleApolloConfig {
  private static final Logger logger = LoggerFactory.getLogger(SimpleApolloConfig.class);
  private String DEFAULT_VALUE = "undefined";
  private Config appConfig; //应用下的私用的配置(application)
  private Map<String, Config> configMaps = new HashMap<>();

  public SimpleApolloConfig(ApolloModel apolloModel) {
//      -Denv=dev -Ddev_meta=http://192.168.0.39:8080
    System.setProperty("env", apolloModel.getEnv());
    System.setProperty("dev_meta", apolloModel.getMetaUrl());
    System.setProperty("app.id", apolloModel.getAppId());
    List<String> nameSpaceList= apolloModel.getNameSpaces();

    ConfigChangeListener changeListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        logger.info("Changes for namespace {}", changeEvent.getNamespace());
        for (String key : changeEvent.changedKeys()) {
          ConfigChange change = changeEvent.getChange(key);
          logger.warn("Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
              change.getPropertyName(), change.getOldValue(), change.getNewValue(),
              change.getChangeType());
          // need code ???
//              configMaps.put(changeEvent.getNamespace(), change);
        }
      }
    };

//    application
    appConfig = ConfigService.getAppConfig();
    appConfig.addChangeListener(changeListener);

    if(null != nameSpaceList && !nameSpaceList.isEmpty()) {
      for(String nameSpace : nameSpaceList) {
        if("application".equalsIgnoreCase(nameSpace)){
          continue;
        }
        //  根据传入的命名空间集合获取配置，也就是所有公用，私用，关联的配置
        Config publicConfig =ConfigService.getConfig(nameSpace);
        if(null !=publicConfig) {
          configMaps.put(nameSpace, publicConfig);
          publicConfig.addChangeListener(changeListener);
          Set<String> configSet =  publicConfig.getPropertyNames();
          if(!configSet.isEmpty()) {
              for(Iterator<String> iterator = configSet.iterator(); iterator.hasNext();){
                String key = iterator.next();
                logger.info("{} config key: {}", nameSpace, key);
              }
          }
      }
    }
    }

  }

  /**
   * 根据关键字取配置信息值
   * 先取私有空间的，再取公有或关联的,取公有或关联里第一个匹配的信息返回并退出
   * @param key   关键字
   * @return
   */
  public String getConfig(String key) {
    String result = appConfig.getProperty(key, DEFAULT_VALUE);
    if(DEFAULT_VALUE.equals(result)) {
      for(String nameSpace : configMaps.keySet()) {
        Config config = configMaps.get(nameSpace);
        if(null != config) {
          result = config.getProperty(key, DEFAULT_VALUE);
          if(!DEFAULT_VALUE.equals(result)) { break; }
        }
      }
    }
    logger.warn(String.format("Loading key : %s with value: %s", key, result));
    return DEFAULT_VALUE.equals(result) ? "" : result;
  }

//  public static void main(String[] args) throws IOException {
//    SimpleApolloConfig apolloConfigDemo = new SimpleApolloConfig();
//    System.out.println(
//        "Apollo Config Demo. Please input key to get the value. Input quit to exit.");
//    while (true) {
//      System.out.print("> ");
//      String input = new BufferedReader(new InputStreamReader(System.in, Charsets.UTF_8)).readLine();
//      if (input == null || input.length() == 0) {
//        continue;
//      }
//      input = input.trim();
//      if (input.equalsIgnoreCase("quit")) {
//        System.exit(0);
//      }
//      apolloConfigDemo.getConfig(input);
//    }
//  }
}
