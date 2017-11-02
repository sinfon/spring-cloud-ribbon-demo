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

---



