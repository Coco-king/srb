package top.codecrab.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author codecrab
 * @since 2021年04月26日 17:23
 */
@SpringBootApplication
@ComponentScan({"top.codecrab.srb"})
public class ServiceOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
