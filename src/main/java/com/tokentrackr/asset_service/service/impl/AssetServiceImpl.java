package com.tokentrackr.asset_service.service.impl;

import com.tokentrackr.asset_service.dto.AssetDTO;
import com.tokentrackr.asset_service.dto.CreateAssetRequest;
import com.tokentrackr.asset_service.entity.Asset;
import com.tokentrackr.asset_service.exception.AssetNotFoundException;
import com.tokentrackr.asset_service.repository.AssetRepository;
import com.tokentrackr.asset_service.service.interfaces.AssetService;
import com.tokentrackr.asset_service.service.interfaces.PriceCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final PriceCacheService priceCacheService;

    @Override
    public AssetDTO createAsset(CreateAssetRequest request, String userId) {
        Asset asset = new Asset();
        asset.setCryptoId(request.getCryptoId().toUpperCase());
        asset.setUserId(userId);
        asset.setQuantity(request.getQuantity());

        Asset savedAsset = assetRepository.save(asset);
        log.info("Created asset with id: {} for user: {}", savedAsset.getId(), userId);
        return convertToDTO(savedAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetDTO> getUserAssets(String userId) {
        List<Asset> assets = assetRepository.findByUserId(userId);

        // Get all unique symbols from user's assets
        Set<String> symbols = assets.stream()
                .map(Asset::getCryptoId)
                .collect(Collectors.toSet());

        // Fetch all prices at once from Redis
        Map<String, Double> prices = priceCacheService.getPrices(symbols);

        // Convert to DTOs with prices
        return assets.stream()
                .map(asset -> convertToDTO(asset, prices.get(asset.getCryptoId())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAsset(Long id, String userId) {
        Asset asset = assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found with id: " + id));
        assetRepository.delete(asset);
        log.info("Deleted asset with id: {} for user: {}", id, userId);
    }

    @Override
    public AssetDTO increaseQuantity(Long id, Double quantity, String userId) {
        Asset asset = assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found with id: " + id));

        asset.setQuantity(asset.getQuantity() + quantity);
        Asset updatedAsset = assetRepository.save(asset);
        log.info("Increased quantity by {} for asset id: {}", quantity, id);
        return convertToDTO(updatedAsset);
    }

    @Override
    public void deleteAllAssetsByUserId(String userId) {
        assetRepository.deleteByUserId(userId);
        log.info("Deleted all assets for user: {}", userId);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No authenticated user found");
        }
        Jwt jwt = (Jwt) auth.getPrincipal();
        return jwt.getSubject(); // “sub” claim, which is Keycloak’s user ID
    }

    private AssetDTO convertToDTO(Asset asset) {
        Double currentPrice = priceCacheService.getPrice(asset.getCryptoId());
        return convertToDTO(asset, currentPrice);
    }

    private AssetDTO convertToDTO(Asset asset, Double currentPrice) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setUserId(asset.getUserId());
        dto.setUserId(asset.getUserId());
        dto.setQuantity(asset.getQuantity());
        // Set current price from Redis cache
        dto.setCurrentPrice(currentPrice != null ? currentPrice : 0.0);

        return dto;
    }
}
