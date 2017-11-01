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
    private static final String virtualHostName = "demo-server";
    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;

    @Autowired
    public DemoController(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
    }

    @GetMapping("/list")
    public ResponseEntity<String> show() {
        String result = restTemplate.getForObject("http://demo-server/v1/demo", String.class);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/instance")
    public void logInstance() {
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

虚拟主机名可以 **自动映射** 为对应的服务地址，多次访问 `/instance` 输出如下

```
ServiceId: demo-server; Host: demo.com; Port: 20084
ServiceId: demo-server; Host: demo.com; Port: 10084
ServiceId: demo-server; Host: demo.com; Port: 20084
ServiceId: demo-server; Host: demo.com; Port: 10084
```

不难发现，如果不进行额外配置，默认会以 **轮询** 的方式访问服务

**PS** 默认情况下，虚拟主机名与服务名一致，如需指定。可在服务端修改如下配置

```
eureka:
  instance:
    virtual-host-name: demo-server-name
```

通过 Ribbon 访问服务，以虚拟主机名区分是否为同一个服务

---

## 脱离注册中心使用

```
server-name:
  ribbon:
    listOfServers: http://demo.com:10084,http://demo.com:20084
```

**PS** 仅适用于当前客户端未在注册中心注册（或者没有注册中心），但是需要使用 Ribbon 的场景。

---

## 自定义配置

### 负载均衡规则

```
@Configuration
public class DemoServerRibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
```
```
@Configuration
@RibbonClient(name = "ashman", configuration = DemoServerRibbonConfiguration.class)
public class DemoServerRibbonConfig {
}
```












## QA

通过Docker将同样的服务绑定在不同的外部端口（内部端口相同），则无法实现轮询

- eureka上绑定信息感知不到外部端口不同
- 那么该如何使用docker部署多个微服务呢
- 每台机器上一整套，而不是一台机器上多个同一种服务？


