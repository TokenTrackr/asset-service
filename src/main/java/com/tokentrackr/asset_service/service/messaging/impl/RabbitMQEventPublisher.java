package com.tokentrackr.asset_service.service.messaging.impl;

import com.tokentrackr.asset_service.dto.events.AssetUpdateFailedEvent;
import com.tokentrackr.asset_service.dto.events.AssetUpdatedEvent;
import com.tokentrackr.asset_service.service.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String SAGA_EXCHANGE = "saga.direct.exchange";

    private static final String ASSET_UPDATED_ROUTING_KEY = "asset.updated";
    private static final String ASSET_UPDATE_FAILED_ROUTING_KEY = "asset.update.failed";

    @Override
    public void publishAssetUpdated(AssetUpdatedEvent event) {
        rabbitTemplate.convertAndSend(SAGA_EXCHANGE, ASSET_UPDATED_ROUTING_KEY, event);
    }

    @Override
    public void publishAssetUpdateFailed(AssetUpdateFailedEvent event) {
        rabbitTemplate.convertAndSend(SAGA_EXCHANGE, ASSET_UPDATE_FAILED_ROUTING_KEY, event);
    }
}
