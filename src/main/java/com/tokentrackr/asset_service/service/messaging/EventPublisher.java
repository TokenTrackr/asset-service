package com.tokentrackr.asset_service.service.messaging;

import com.tokentrackr.asset_service.dto.events.AssetUpdateFailedEvent;
import com.tokentrackr.asset_service.dto.events.AssetUpdatedEvent;

public interface EventPublisher {
    void publishAssetUpdated(AssetUpdatedEvent event);
    void publishAssetUpdateFailed(AssetUpdateFailedEvent event);
}
