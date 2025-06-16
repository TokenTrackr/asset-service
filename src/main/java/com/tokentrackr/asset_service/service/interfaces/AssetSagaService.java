package com.tokentrackr.asset_service.service.interfaces;

import com.tokentrackr.asset_service.dto.events.AssetUpdateEvent;

public interface AssetSagaService {
    public void processAssetUpdate(AssetUpdateEvent event);
}
