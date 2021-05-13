package top.codecrab.srb.amqp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author codecrab
 * @since 2021年05月13日 9:26
 */
@Slf4j
@Service
public class AmqpService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由
     * @param message    消息
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        log.info("发送消息...........");
        amqpTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
