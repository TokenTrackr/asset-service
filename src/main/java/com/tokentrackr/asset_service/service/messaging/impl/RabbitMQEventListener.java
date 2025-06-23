package com.tokentrackr.asset_service.service.messaging.impl;

import com.tokentrackr.asset_service.dto.events.AssetUpdateEvent;
import com.tokentrackr.asset_service.service.interfaces.AssetSagaService;
import com.tokentrackr.asset_service.service.messaging.EvenListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventListener implements EvenListener {
    private final AssetSagaService assetSagaService;


    @RabbitListener(queues = "asset.update.queue")
    @Override
    public void handleAssetUpdate(AssetUpdateEvent event) {
        log.info("Received asset update event: {}", event.getSagaId());
        assetSagaService.processAssetUpdate(event);
    }
}
