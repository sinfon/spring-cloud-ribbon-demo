# Spring Cloud Ribbon 简介

- 基于 HTTP 和 TCP 的 **客户端** 负载均衡工具
- 基于 Netflix Ribbon 实现，通过 Spring Cloud 封装

## 依赖

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

`spring-cloud-starter-eureka` 已包含必要依赖：
- `spring-boot-starter-web`
- `spring-cloud-starter`
- `spring-cloud-netflix-eureka-client`
- `spring-cloud-starter-ribbon`


## 使用

### 创建负载均衡的 RestTemplate 对象

```java
@Configuration
public class MyConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    public String doOtherStuff() {
        String results = restTemplate.getForObject("http://stores/stores", String.class);
        return results;
    }
}
```

### 创建不同需求的 RestTemplate 对象

If you want a RestTemplate that is not load balanced, create a RestTemplate bean and inject it as normal. 
To access the load balanced RestTemplate use the @LoadBalanced qualifier when you create your @Bean.

```java
@Configuration
public class MyConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate loadBalanced() {
        return new RestTemplate();
    }

    @Primary
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @LoadBalanced
    private RestTemplate loadBalanced;

    public String doOtherStuff() {
        return loadBalanced.getForObject("http://stores/stores", String.class);
    }

    public String doStuff() {
        return restTemplate.getForObject("http://example.com", String.class);
    }
}
```

### 直接使用 Ribbon API

```java
public class MyClass {
    @Autowired
    private LoadBalancerClient loadBalancer;

    public void doStuff() {
        ServiceInstance instance = loadBalancer.choose("stores");
        URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
        // ... do something with the URI
    }
}
```

## 虚拟主机名

虚拟主机名可以 **自动映射** 为对应的服务地址，多次访问 `/demo/instance` 输出如下

```
ServiceId: demoservice; Host: demo.com; Port: 20088
ServiceId: demoservice; Host: demo.com; Port: 10088
ServiceId: demoservice; Host: demo.com; Port: 20088
ServiceId: demoservice; Host: demo.com; Port: 10088
```

不难发现，如果不进行额外配置，默认会以 **轮询** 的方式访问服务

**PS** 默认情况下，虚拟主机名与服务名一致，如需指定。可在服务端修改如下配置

```
eureka:
  instance:
    virtual-host-name: demoservice-test
```

通过 Ribbon 访问服务，以虚拟主机名区分是否为同一个服务


## 脱离注册中心使用

注册中心并非 Ribbon 使用的必要条件，在无注册中心的情况下，可通过下面配置，设置 Ribbon 的服务访问列表

```
demoservice:
  ribbon:
    listOfServers: http://demo.com:10088,http://demo.com:20088
```

适用场景：
- 无注册中心
- 未在注册中心注册
- Ribbon 禁用了注册中心

### 配置 Ribbon 禁用 Eureka

```
ribbon:
  eureka:
   enabled: false
```


## 自定义配置

```java
@Configuration
public class FooConfiguration {
    @Bean
    public IPing ribbonPing(IClientConfig config) {
        return new PingUrl();
    }
}
```

> The FooConfiguration has to be @Configuration but take care that it is not in a @ComponentScan 
> for the main application context, otherwise it will be shared by all the @RibbonClients. 
> 
> If you use @ComponentScan (or @SpringBootApplication) you need to take steps 
> to avoid it being included (for instance put it in a separate, non-overlapping package, 
> or specify the packages to scan explicitly in the @ComponentScan).

### @RibbonClient

```java
@Configuration
@RibbonClient(name = "foo", configuration = FooConfiguration.class)
public class TestConfiguration {
}
```

### @RibbonClients

```java
@Configuration
@RibbonClients(value = {
        @RibbonClient(name = "foo", configuration = FooConfiguration.class),
        @RibbonClient(name = "bar", configuration = BarConfiguration.class)
})
public class TestConfiguration {
}
```

### 配置介绍

默认配置 `org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration`

- Rule 
    - 逻辑组件，用于判定返回服务列表中的哪一个
- Ping 
    - 后台运行，确认各个服务的存活状态
- ServerList 
    - 可以被静态或动态指定，动态指定时，后台线程会以特定时间间隔，刷新并过滤服务列表
    - 动态指定 `DynamicServerListLoadBalancer`


Spring Cloud Netflix provides the following beans by default for ribbon (BeanType beanName: ClassName):
- IClientConfig ribbonClientConfig: DefaultClientConfigImpl
- IRule ribbonRule: ZoneAvoidanceRule
- IPing ribbonPing: NoOpPing
- ServerList<Server> ribbonServerList: ConfigurationBasedServerList
- ServerListFilter<Server> ribbonServerListFilter: ZonePreferenceServerListFilter
- ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer
- ServerListUpdater ribbonServerListUpdater: PollingServerListUpdater


### 通过属性自定义 Ribbon 客户端

The supported properties are listed below and should be prefixed by <clientName>.ribbon.:
- NFLoadBalancerClassName: should implement ILoadBalancer
- NFLoadBalancerRuleClassName: should implement IRule
- NFLoadBalancerPingClassName: should implement IPing
- NIWSServerListClassName: should implement ServerList
- NIWSServerListFilterClassName should implement ServerListFilter

> Classes defined in these properties have precedence over beans 
> defined using @RibbonClient(configuration=MyRibbonConfig.class) and the defaults provided by Spring Cloud Netflix.


## Caching of Ribbon Configuration
   
Each Ribbon named client has a corresponding child Application Context that Spring Cloud maintains, 
this application context is lazily loaded up on the first request to the named client. 
This lazy loading behavior can be changed to instead eagerly load up these child Application contexts 
at startup by specifying the names of the Ribbon clients.

```yaml
ribbon:
  eager-load:
    enabled: true
    clients: client1, client2, client3
```
   
## 问题

- [ ] 访问使用的虚拟主机名与 @RibbonClient 名称的配置存在一些不理解的问题，比如大小写，具体指代之类

## 参考链接

- [Spring Cloud](http://cloud.spring.io/spring-cloud-static/Dalston.SR3/)

