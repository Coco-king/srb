package top.codecrab.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author codecrab
 * @since 2021年04月26日 15:01
 */
@SpringBootApplication
@ComponentScan({"top.codecrab.srb"})
public class ServiceSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class, args);
    }
}
