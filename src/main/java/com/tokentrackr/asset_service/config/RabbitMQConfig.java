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

//    // Existing crypto/price queue setup
//    @Value("${price.queue.name}")
//    private String queueName;
//
//    @Value("${price.exchange.name}")
//    private String exchangeName;
//
//    @Value("${price.routing.key}")
//    private String routingKey;
//
//    @Bean
//    public Queue cryptoQueue() {
//        return new Queue(queueName, true);
//    }
//
//    @Bean
//    public DirectExchange cryptoExchange() {
//        return new DirectExchange(exchangeName, true, false);
//    }
//
//    @Bean
//    public Binding cryptoBinding(Queue cryptoQueue, DirectExchange cryptoExchange) {
//        return BindingBuilder.bind(cryptoQueue).to(cryptoExchange).with(routingKey);
//    }

    // --- NEW: Saga-related queues and exchange setup ---

    public static final String SAGA_EXCHANGE = "saga.direct.exchange";

    public static final String ASSET_UPDATE_QUEUE = "asset.update.queue";
    public static final String ASSET_UPDATED_QUEUE = "asset.updated.queue";
    public static final String ASSET_UPDATE_FAILED_QUEUE = "asset.update.failed.queue";

    public static final String ASSET_UPDATE_ROUTING_KEY = "asset.update";
    public static final String ASSET_UPDATED_ROUTING_KEY = "asset.updated";
    public static final String ASSET_UPDATE_FAILED_ROUTING_KEY = "asset.update.failed";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue assetUpdateQueue() {
        return QueueBuilder.durable(ASSET_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue assetUpdatedQueue() {
        return QueueBuilder.durable(ASSET_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue assetUpdateFailedQueue() {
        return QueueBuilder.durable(ASSET_UPDATE_FAILED_QUEUE).build();
    }

    @Bean
    public Binding assetUpdateBinding() {
        return BindingBuilder.bind(assetUpdateQueue())
                .to(sagaExchange())
                .with(ASSET_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding assetUpdatedBinding() {
        return BindingBuilder.bind(assetUpdatedQueue())
                .to(sagaExchange())
                .with(ASSET_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding assetUpdateFailedBinding() {
        return BindingBuilder.bind(assetUpdateFailedQueue())
                .to(sagaExchange())
                .with(ASSET_UPDATE_FAILED_ROUTING_KEY);
    }

    // Common JSON converter and RabbitTemplate setup
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

