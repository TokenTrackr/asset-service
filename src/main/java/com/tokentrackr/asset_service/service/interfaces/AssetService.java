package com.tokentrackr.asset_service.service.interfaces;

import com.tokentrackr.asset_service.dto.AssetDTO;
import com.tokentrackr.asset_service.dto.CreateAssetRequest;
import java.util.List;

public interface AssetService {
    AssetDTO createAsset(CreateAssetRequest request, String userId);
    List<AssetDTO> getUserAssets(String userId);
    void deleteAsset(Long id, String userId);
    AssetDTO increaseQuantity(Long id, Double quantity, String userId);
    void deleteAllAssetsByUserId(String userId);
}