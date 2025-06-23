package com.tokentrackr.asset_service.repository;

import com.tokentrackr.asset_service.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByUserId(String userId);
    Optional<Asset> findByIdAndUserId(UUID assetId, String userId);

    void deleteByUserId(String userId);

    Optional<Asset> findByUserIdAndCryptoId(String userId, String cryptoId);
}
