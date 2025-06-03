package com.tokentrackr.asset_service.listener;

import com.rabbitmq.stream.Message;
import com.tokentrackr.asset_service.service.interfaces.AssetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletionEventListener {
    private final AssetService assetService;
    private final RabbitTemplate rabbitTemplate;

    // Pull queue/exchange/routing-key names from application.properties
    @Value("${rabbitmq.user-deleted-queue}")
    private String userDeletedQueue;

    @Value("${rabbitmq.assets-deletion-success-exchange}")
    private String assetsDeletionSuccessExchange;

    @Value("${rabbitmq.assets-deletion-success-routing-key}")
    private String assetsDeletionSuccessRoutingKey;

    @Value("${rabbitmq.assets-deletion-failure-exchange}")
    private String assetsDeletionFailureExchange;

    @Value("${rabbitmq.assets-deletion-failure-routing-key}")
    private String assetsDeletionFailureRoutingKey;

    /**
     * We declare the queue here if it doesn’t already exist.
     * (This is equivalent to ‟queuesToDeclare = @Queue(USER_DELETED_QUEUE)”, but we’ll
     * reference the property instead of a hardcoded constant.)
     */
    @RabbitListener(queuesToDeclare = @Queue("${rabbitmq.user-deleted-queue}"))
    @Transactional
    public void onUserAccountDeleted(Message message) {
        String userId = new String((byte[]) message.getBody(), StandardCharsets.UTF_8);
        log.info("Received UserDeletionEvent for userId={}", userId);

        try {
            assetService.deleteAllAssetsByUserId(userId);

            rabbitTemplate.convertAndSend(
                    assetsDeletionSuccessExchange,
                    assetsDeletionSuccessRoutingKey,
                    userId
            );
            log.info("Published AssetDeletionSuccessEvent for userId={}", userId);

        } catch (Exception ex) {
            log.error("Failed to delete all assets for userId={}. Sending failure event.", userId, ex);

            rabbitTemplate.convertAndSend(
                    assetsDeletionFailureExchange,
                    assetsDeletionFailureRoutingKey,
                    userId
            );

            throw ex; // Rethrow so the transaction is rolled back and the message can be retried
        }
    }
}

