package com.tokentrackr.asset_service.service.impl;

import com.tokentrackr.asset_service.dto.events.AssetUpdateEvent;
import com.tokentrackr.asset_service.dto.events.AssetUpdateFailedEvent;
import com.tokentrackr.asset_service.dto.events.AssetUpdatedEvent;
import com.tokentrackr.asset_service.entity.Asset;
import com.tokentrackr.asset_service.enums.TransactionType;
import com.tokentrackr.asset_service.exception.AssetNotFoundException;
import com.tokentrackr.asset_service.exception.InsufficientAssetException;
import com.tokentrackr.asset_service.repository.AssetRepository;
import com.tokentrackr.asset_service.service.interfaces.AssetSagaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetSagaServiceImpl implements AssetSagaService {
    private final AssetRepository assetRepository;

    @Override
    public void processAssetUpdate(AssetUpdateEvent event) {
        try {
            if (event.isCompensation()) {
                handleCompensation(event);
            } else {
                handleRegularUpdate(event);
            }
            publishSuccess(event);
        } catch (Exception ex) {
            log.error("Asset update failed: {}", ex.getMessage());
            publishFailure(event, ex.getMessage());
        }
    }

    private void handleRegularUpdate(AssetUpdateEvent event) {
        Optional<Asset> existingAsset = assetRepository.findByUserIdAndCryptoId(
                event.getUserId(),
                event.getCryptoId()
        );

        switch (event.getTransactionType()) {
            case BUY:
                handleBuy(event, existingAsset);
                break;
            case SELL:
                handleSell(event, existingAsset);
                break;
        }
    }

    private void handleCompensation(AssetUpdateEvent event) {
        // Reverse the operation type for compensation
        TransactionType reversedType = event.getTransactionType() == TransactionType.BUY
                ? TransactionType.SELL
                : TransactionType.BUY;

        AssetUpdateEvent compensationEvent = new AssetUpdateEvent(
                event.getSagaId(),
                event.getTransactionId(),
                event.getUserId(),
                event.getCryptoId(),
                event.getQuantity(),
                reversedType,
                false  // Not a compensation of a compensation
        );

        handleRegularUpdate(compensationEvent);
    }

    private void handleBuy(AssetUpdateEvent event, Optional<Asset> existingAsset) {
        if (existingAsset.isPresent()) {
            Asset asset = existingAsset.get();
            asset.setQuantity(asset.getQuantity().add(event.getQuantity()));
            assetRepository.save(asset);
        } else {
            createNewAsset(event);
        }
    }

    private void handleSell(AssetUpdateEvent event, Optional<Asset> existingAsset) {
        Asset asset = existingAsset.orElseThrow(() ->
                new AssetNotFoundException("Asset not found for crypto: " + event.getCryptoId())
        );

        if (asset.getQuantity().compareTo(event.getQuantity()) < 0) {
            throw new InsufficientAssetException(
                    "Insufficient quantity. Available: " + asset.getQuantity() +
                            ", Required: " + event.getQuantity()
            );
        }

        asset.setQuantity(asset.getQuantity().subtract(event.getQuantity()));
        assetRepository.save(asset);
    }

    private void createNewAsset(AssetUpdateEvent event) {
        Asset newAsset = Asset.builder()
                .cryptoId(event.getCryptoId())
                .userId(event.getUserId())
                .quantity(event.getQuantity())
                .build();
        assetRepository.save(newAsset);
    }

    private void publishSuccess(AssetUpdateEvent event) {
        eventPublisher.publishAssetUpdated(
                AssetUpdatedEvent.builder()
                        .sagaId(event.getSagaId())
                        .transactionId(event.getTransactionId())
                        .userId(event.getUserId())
                        .success(true)
                        .build()
        );
    }

    private void publishFailure(AssetUpdateEvent event, String reason) {
        eventPublisher.publishAssetUpdateFailed(
                AssetUpdateFailedEvent.builder()
                        .sagaId(event.getSagaId())
                        .transactionId(event.getTransactionId())
                        .userId(event.getUserId())
                        .failureReason(reason)
                        .build()
        );
    }
}
