package top.sinfonia.demo.ribbon.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 14:24<br>
 * <b>Author:</b> Asher<br>
 */
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

    @GetMapping("/demo/ip")
    public ResponseEntity<String> demoIp() {
        String result = restTemplate.getForObject("http://demoservice/ip", String.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/demo/info")
    public ResponseEntity<String> demoInfo() {
        String result = restTemplate.getForObject("http://demoservice/info", String.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<String> show() {
        String result = restTemplate.getForObject("http://ashman/v1/ashman", String.class);
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

    @GetMapping("/instance")
    public void logInstance() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("ashman");
        LOGGER.info("ServiceId: {}; Host: {}; Port: {}",
                serviceInstance.getServiceId(),
                serviceInstance.getHost(),
                serviceInstance.getPort()
        );
    }
}
