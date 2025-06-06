package com.tokentrackr.asset_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${price.queue.name}")
    private String queueName;

    @Value("${price.exchange.name}")
    private String exchangeName;

    @Value("${price.routing.key}")
    private String routingKey;

    @Bean
    public Queue cryptoQueue() { return new Queue(queueName, true); }

    @Bean
    public DirectExchange cryptoExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding binding(Queue cryptoQueue, DirectExchange cryptoExchange) {
        return BindingBuilder.bind(cryptoQueue).to(cryptoExchange).with(routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
}
