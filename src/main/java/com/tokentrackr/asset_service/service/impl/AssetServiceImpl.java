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

import java.math.BigDecimal;
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

    @Transactional(readOnly = true)
    public List<AssetDTO> getUserAssets(String userId) {
        List<Asset> assets = assetRepository.findByUserId(userId);

        // Get all unique symbols from user's assets
        Set<String> symbols = assets.stream()
                .map(Asset::getCryptoId)
                .collect(Collectors.toSet());

        // Fetch all prices at once from Redis
        Map<String, BigDecimal> prices = priceCacheService.getPrices(symbols);

        // Convert to DTOs with prices
        return assets.stream()
                .map(asset -> convertToDTO(asset, prices.get(asset.getCryptoId())))
                .collect(Collectors.toList());
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
        BigDecimal currentPrice = priceCacheService.getPrice(asset.getCryptoId());
        return convertToDTO(asset, currentPrice);
    }

    private AssetDTO convertToDTO(Asset asset, BigDecimal currentPrice) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setUserId(asset.getUserId());
        dto.setUserId(asset.getUserId());
        dto.setQuantity(asset.getQuantity());
        // Set current price from Redis cache
        dto.setCurrentPrice(currentPrice != null ? currentPrice : BigDecimal.ZERO);

        return dto;
    }
}
