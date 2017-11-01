package top.sinfonia.demo.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringCloudRibbonDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudRibbonDemoApplication.class, args);
	}
}
