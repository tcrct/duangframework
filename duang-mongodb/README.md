#duangframework MongoDB插件

在duangframework里对外提供了一个使用的MongoDB操作工具类， 再将使用方法简介如下：

##工具类使用方法

1，首先在pom.xml文件里添加Maven依赖

    <dependency>
        <groupId>com.duangframework.mongodb</groupId>
        <artifactId>duang-mongodb</artifactId>
        <version>${mongodb.version}</version>
    </dependency>
    
2，在duang.properator文件里添加如下代码
    
     mongodb.host=192.168.0.39
     mongodb.port=27017
     mongodb.databasename=qingbean

3，在Duang.java文件里添加如下代码
    
    /**
     * add plugin
     */
      @Override
      public void addPlugins() {
        InstanceFactory.setPlugin(new MongodbPlugin());
      }

4，Entity类里要继承IdEntity.java及添@Entity注解
    
    @Entity(name = "user")
    public class User extends IdEntity {
       // 省略get/set方法
    }
    
5, 在Controller或Service类里添加依赖，注入MongoDao对象

    public class UserService {
        @Import
        private MongoDao<User> userDao;
    }
    
    
    