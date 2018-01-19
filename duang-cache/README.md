#duangframework缓存插件

在duangframework里对外提供了一个使用的缓存层操作工具类，该工具类目前仅封装了Redis，后续将Memcache也进行封装。至于使用注解的方式，归入第二期开发时提供。

##工具类使用方法

    CacheKit.duang().set(String key, Object value);
        

以上的使用方法，看名字大概也能知道其用处了，这里就不再进行描述。

## 使用步骤

1，首先在pom.xml文件里添加
