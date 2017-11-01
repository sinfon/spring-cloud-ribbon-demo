package top.sinfonia.demo.ribbon.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 19:31<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
@RibbonClient(name = "ashman", configuration = AshmanRibbonConfiguration.class)
public class AshmanRibbonConfig {
}
