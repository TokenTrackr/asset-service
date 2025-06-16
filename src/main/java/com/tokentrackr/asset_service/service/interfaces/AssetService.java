package com.tokentrackr.asset_service.service.interfaces;

import com.tokentrackr.asset_service.dto.AssetDTO;
import com.tokentrackr.asset_service.dto.CreateAssetRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {

    void deleteAllAssetsByUserId(String userId);
}