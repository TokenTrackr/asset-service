package com.tokentrackr.asset_service.service.messaging;

import com.tokentrackr.asset_service.dto.events.AssetUpdateEvent;

public interface EvenListener {
    public void handleAssetUpdate(AssetUpdateEvent event);
}
