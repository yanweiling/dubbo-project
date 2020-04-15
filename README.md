**1.Dubbo框架**

`dubbo框架是阿里巴巴SOA服务治理方案的核心框架，每天为2000个服务提供30亿次的访问量支持，并被广泛应用于阿里巴巴集团的各成员站点。dubbo是一个分布式服务框架，致力于提供高性能和透明化的RPC远程服务调用方案，以及SOA服务治理方案；
`
![dubbo 流程](http://dubbo.apache.org/docs/en-us/user/sources/images/dubbo-architecture.jpg)


### 组件说明

| 组件名称 |    价格    |
| :------: | :--------: |
| registry |  注册中心  |
| provider | 服务提供者 |
| consumer | 服务消费者 |

	服务提供者启动后，将服务接口注册到注册中心上（这里注册中心我们用zookeeper）
	服务消费者订阅注册中心上的服务，将服务提供者的服务列表信息拉取到服务消费方的缓存中，（服务列表包括：服务提供方的ip、port、 接口等参数）
	注册中心上的服务列表信息发生变化后，将时时更新消费方中的服务列表信息
	当消费者调用接口时，从服务列表中获取具体的ip，port，之后再直接请求远程服务提供方【也可以在消费端指定具体的服务ip，port，不经过服务列表筛选，直接请求服务提供方】


#### 2.如何实现远程通讯

**远程通信：Webservice、restful、dubbo**
1、Webservice：效率不高基于soap协议，其主要的特点是跨语言、跨平台的。项目中不推荐使用，可用于不同公司间接口的调用。
2、使用restful形式的服务：http+json。很多项目中应用。如果服务太多，服务之间调用关系混乱，需要治疗服务。
3、使用dubbo。使用rpc协议进行远程调用，直接使用socket通信。传输效率高，并且可以统计出系统之间的调用关系、调用次数。使用Java语言开发，只能用于Java语言开发的项目间的通信，不具备跨语言，跨平台的特点！

4、dubbo框架支持dubbo协议，http协议，rmi协议，webservice协议
5、执行效率上dubbo协议>http协议>webservice协议，dubbo协议底层采用的是socket通讯

`此外，dubbo支持多版本接口，负载均衡，集群管理，异步调用，请求回调等，而restful，webserivce是不支持这些功能的`

##### 例子演示

**新建项目dubbo-project**

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/jiagou.png?raw=true)

**1.dubbo-rpc**  此项目中存储对外提供的接口，以及接口所需要的其他类，如图

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/dubbo-rpc.png?raw=true)

**pom配置文件**

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.ywl.study</groupId>
    <artifactId>dubbo-rpc</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>1.8</java.version>
    </properties>
    <!--发布配置-->
    <distributionManagement>
        <repository>
            <id>releases</id>
            <name>Nexus Repository</name>
            <url>http://ip:port/nexus/content/repositories/releases/</url>
        </repository>
        <snapshotRepository>
            <id>snapshots</id>
            <name>Nexus Repository</name>
            <url>http://ip:port/nexus/content/repositories/snapshots</url>
        </snapshotRepository>
    </distributionManagement>
    <dependencies>
        ......
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

```
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City  implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 城市编号
     */
    private Long id;

    /**
     * 省份编号
     */
    private Long provinceId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;


}
```

```
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class User implements Serializable {
    private int id;
    private String username;
    private String password;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```

```
public interface CityDubboService {
    /**
     * 根据城市名称，查询城市信息
     * @param cityName
     */
    City findCityByName(String cityName);
}
```

```
public interface UserService {
    User saveUser(User user);
}
```

rpc接口写完以后，将接口、实体类打包成jar，并发布到maven私服上

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/deploy.png?raw=true)

登录私服，可以查看到已经上传的jar包

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/maven.png?raw=true)



------

**2.spring-boot-provider 服务提供者**

目录结构：

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/provider.png?raw=true)



1.pom中引入rpc接口

```
<dependency>
   <groupId>com.ywl.study</groupId>
   <artifactId>dubbo-rpc</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```

2.接口实现类

```
import com.alibaba.dubbo.config.annotation.Service;
import com.ywl.study.rpc.domain.City;
import com.ywl.study.rpc.service.CityDubboService;
@Service
public class CityDubboServiceImpl implements CityDubboService {
    public City findCityByName(String cityName) {
        return new City(1L,2L,"广州","是我的故乡");
    }
}
```

```
import com.alibaba.dubbo.config.annotation.Service;
import com.ywl.study.rpc.domain.User;
import com.ywl.study.rpc.service.UserService;

@Service
public class UserServiceImpl  implements UserService {
    @Override
    public User saveUser(User user) {
        user.setId(1);
        System.out.println(user.toString());
        return user;
    }
}
```

**application.properties**

```
server.port=8081

## Dubbo 服务提供者配置
spring.dubbo.application.name=provider
spring.dubbo.registry.address=zookeeper://106.13.99.195:2181
spring.dubbo.protocol.name=dubbo
spring.dubbo.protocol.port=2088
spring.dubbo.scan=com.ywl.study.service
```

**说明**

```
1.spring.dubbo.application.name ---服务注册名称
2.spring.dubbo.registry.address----服务注册中心
3.spring.dubbo.protocol.name----dubbo框架协议：dubbo协议（同时也支持其他协议）
4.spring.dubbo.protocol.port---端口号
5.spring.dubbo.scan-----发布服务所在包
```

**启动类**

```
@SpringBootApplication
public class SpringBootProviderApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringBootProviderApplication.class, args);
   }

}
```

当服务启动以后，登录到dubbo-admin控制平台，http://106.13.99.195:8080/dubbo-admin-2.5.10/，我们可以看到启动的服务已经注册到注册中心zookeeper中了

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/provider-controller.png?raw=true)

点击provicer进去

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/provider-controler-1.png?raw=true)

点击接口进去

![](https://github.com/yanweiling/img-floder/blob/master/cloudimg/iplist.png?raw=true)

------

**3.spring-boot-consumer 服务消费者**

![](https://github.com/yanweiling/img-floder2/blob/master/img/consumer1.png?raw=true)

1.pom.xml 引入rpc接口jar

```
<dependency>
   <groupId>com.ywl.study</groupId>
   <artifactId>dubbo-rpc</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```

2.application.properties 将com.ywl.study.service下的rpc接口引入

```
server.port=8082
## Dubbo 服务消费者配置
spring.dubbo.application.name=consumer
spring.dubbo.registry.address=zookeeper://106.13.99.195:2181
spring.dubbo.scan=com.ywl.study.service
```

3.rpc服务调用

```
package com.ywl.study.service;

import com.alibaba.dubbo.config.annotation.Reference;

import com.ywl.study.rpc.domain.City;
import com.ywl.study.rpc.domain.User;
import com.ywl.study.rpc.service.CityDubboService;
import com.ywl.study.rpc.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CityDubboConsumerService {
    @Reference
    CityDubboService cityDubboService;

    @Reference
    UserService userService;

    public void printCity() {
        String cityName = "广州";
        City city = cityDubboService.findCityByName(cityName);
        System.out.println(city.toString());
    }


    public User saveUser() {
        User user = new User();
        user.setUsername("jaycekon")
                .setPassword("jaycekong824");
        return userService.saveUser(user);
    }
}
```

4.controller接口

```
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CityDubboConsumerService service;

    @GetMapping("/save")
    @ResponseBody
    public Object saveUser() {

        return service.saveUser();
    }
}
```

5.消费者启动类

```
@SpringBootApplication
public class SpringBootConsumerApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringBootConsumerApplication.class, args);
   }

}
```

消费者启动后，登录到dubbo-admin控制平台

![](https://github.com/yanweiling/img-floder2/blob/master/img/consumer-controller.png)

![](https://github.com/yanweiling/img-floder2/blob/master/img/consumer-controller2.png?raw=true)

------

最后，我们请求rpc消费方的controller接口：http://localhost:8082/user/save，返回

```
{"id":1,"username":"jaycekon","password":"jaycekong824"}
```
