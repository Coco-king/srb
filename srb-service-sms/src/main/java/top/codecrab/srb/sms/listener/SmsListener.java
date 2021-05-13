package top.codecrab.srb.sms.listener;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codecrab.srb.amqp.config.AmqpConstants;
import top.codecrab.srb.base.entity.dto.SmsDTO;
import top.codecrab.srb.sms.service.SmsService;
import top.codecrab.srb.sms.utils.SmsProperties;

/**
 * @author codecrab
 * @since 2021年05月13日 10:02
 */
@Slf4j
@Component
public class SmsListener {

    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpConstants.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(value = AmqpConstants.EXCHANGE_TOPIC_SMS, ignoreDeclarationExceptions = "true"),
            key = {AmqpConstants.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO) {
        log.info("监听到消息发送：{}", smsDTO);

        smsService.sendSms(
                smsDTO.getMobile(),
                SmsProperties.TEMPLATE_CODE,
                MapUtil.of("code", smsDTO.getMessage())
        );
    }
}
