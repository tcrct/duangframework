package com.duangframework.config.apollo.api;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class SimpleApolloConfig {
  private static final Logger logger = LoggerFactory.getLogger(SimpleApolloConfig.class);
  private String DEFAULT_VALUE = "null";
  private Config config;

  public SimpleApolloConfig(String env, String metaUrl) {
//      -Denv=dev -Ddev_meta=http://192.168.0.39:8080

      System.setProperty("env", env);
      System.setProperty("dev_meta", metaUrl);

    ConfigChangeListener changeListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        logger.info("Changes for namespace {}", changeEvent.getNamespace());
        for (String key : changeEvent.changedKeys()) {
          ConfigChange change = changeEvent.getChange(key);
          logger.info("Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
              change.getPropertyName(), change.getOldValue(), change.getNewValue(),
              change.getChangeType());
        }
      }
    };
//      ConfigService.getConfig(""); //  获取公共Namespace的配置
    config = ConfigService.getAppConfig();
    config.addChangeListener(changeListener);

//    Set<String> configSet =  config.getPropertyNames();
//    if(!configSet.isEmpty()) {
//      for(Iterator<String> iterator = configSet.iterator(); iterator.hasNext();){
//        String key = iterator.next();
//        logger.info("config key: {}", key);
//      }
//    }

  }

  public String getConfig(String key) {
    String result = config.getProperty(key, DEFAULT_VALUE);
    logger.info(String.format("Loading key : %s with value: %s", key, result));
    return result;
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
