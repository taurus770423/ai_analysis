package co.winmon.groupbuy;

import co.winmon.groupbuy.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class WinmonGroupBuyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WinmonGroupBuyBackendApplication.class, args);
    }

}
