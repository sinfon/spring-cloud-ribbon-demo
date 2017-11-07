# Spring Cloud Ribbon

---

基于 HTTP 和 TCP 的 **客户端** 负载均衡工具

基于 Netflix Ribbon 实现，通过 Spring Cloud 封装

---

## 准备工作

### 依赖

只需要 `spring-cloud-starter-eureka`

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

因为 `spring-cloud-starter-eureka` 中已经包含了如下几个必要的依赖：

- `spring-boot-starter-web`
- `spring-cloud-starter`
- `spring-cloud-netflix-eureka-client`
- `spring-cloud-starter-ribbon`
- and so on

### 配置

```
@Configuration
public class WebConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

## 使用

```
@RestController
public class DemoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    private static final String virtualHostName = "demoservice";
    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;

    @Autowired
    public DemoController(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
    }

    @GetMapping("/demo/info")
    public ResponseEntity<String> demoInfo() {
        String result = restTemplate.getForObject("http://demoservice/info", String.class);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/demo/instance")
    public void logDemoInstance() {
        ServiceInstance serviceInstance = loadBalancerClient.choose(virtualHostName);
        LOGGER.info("ServiceId: {}; Host: {}; Port: {}",
                serviceInstance.getServiceId(),
                serviceInstance.getHost(),
                serviceInstance.getPort()
        );
    }
}
```

### 虚拟主机名

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

---

## 脱离注册中心使用

```
demoservice:
  ribbon:
    listOfServers: http://demo.com:10088,http://demo.com:20088
```

**PS** 仅适用于当前客户端未在注册中心注册（或者没有注册中心），但是需要使用 Ribbon 的场景。

---

## 自定义配置

针对每种服务自定义配置，需要将配置文件放在启动类的 ComponentScan 之外

且注意虚拟主机名和 @RibbonClient的名称大小写一致，对大小写敏感

并不是大小写敏感

只有虚拟主机名使用大写，而配置的name使用小写的时候才会无法生效

虚拟主机名小写，配置大写不会有问题

大小写一致不会有问题


### 负载均衡规则

修改为随机

```
@Configuration
@RibbonClient(name = "spring-cloud-ribbon-demo", configuration = RibbonConfig.RibbonConfiguration.class)
public class RibbonConfig {
    @Configuration
    public class RibbonConfiguration {
        @Bean
        public IRule ribbonRule() {
            return new RandomRule();
        }
    }
}
```
### 为不同服务客户端配置不同的负载均衡策略

### 两种配置方式

### 在服务中心注册，但是不通过服务中心使用 ribbon

```yaml
ribbon:
  eureka:
   enabled: false
```
## Caching of Ribbon Configuration
   
Each Ribbon named client has a corresponding child Application Context that Spring Cloud maintains, this application context is lazily loaded up on the first request to the named client. This lazy loading behavior can be changed to instead eagerly load up these child Application contexts at startup by specifying the names of the Ribbon clients.

```yaml
ribbon:
  eager-load:
    enabled: true
    clients: client1, client2, client3
```
   
服务名 虚拟主机名 服务ID
   
---



