package world.xuewei.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "booking.order.queue";
    public static final String EXCHANGE_NAME = "booking.exchange";
    public static final String ROUTING_KEY = "booking.order.key";
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 1. 定义队列 Bean
    @Bean
    public Queue orderQueue() {
        // 使用常量
        return new Queue(QUEUE_NAME, true);
    }
    // 2. 定义交换机 Bean
    @Bean
    public Exchange orderExchange() {
        // 使用常量
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }
    // 3. 队列和交换机绑定
    @Bean
    public Binding bindingOrderQueue(Queue orderQueue, Exchange orderExchange) {
        return BindingBuilder.bind(orderQueue)
                .to(orderExchange)
                .with(ROUTING_KEY) // 使用常量
                .noargs();
    }
}
