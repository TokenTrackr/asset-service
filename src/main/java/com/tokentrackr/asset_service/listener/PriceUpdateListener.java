package com.tokentrackr.asset_service.listener;

import com.tokentrackr.asset_service.dto.PriceUpdateMessage;
import com.tokentrackr.asset_service.service.interfaces.PriceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateListener {

    private final PriceCacheService priceCacheService;

    @RabbitListener(queues = "price.update.queue")
    public void handlePriceUpdate(PriceUpdateMessage message) {
        try {
            log.info("Received price update for {}: {}", message.getCryptoId(), message.getPrice());

            // Cache the price in Redis
            priceCacheService.cachePrice(message.getCryptoId(), message.getPrice());

            log.debug("Successfully cached price for {}: {}", message.getCryptoId(), message.getPrice());
        } catch (Exception e) {
            log.error("Error processing price update for {}: {}", message.getCryptoId(), e.getMessage(), e);
        }
    }
}
