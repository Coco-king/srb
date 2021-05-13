package top.codecrab.srb.amqp.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codecrab
 * @since 2021年05月13日 9:21
 */
@Configuration
public class AmqpConfig {

    @Bean
    public MessageConverter messageConverter() {
        //json字符串转换器
        return new Jackson2JsonMessageConverter();
    }
}
