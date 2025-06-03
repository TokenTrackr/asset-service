package com.tokentrackr.asset_service.repository;

import com.tokentrackr.asset_service.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserId(String userId);
    Optional<Asset> findByIdAndUserId(Long assetId, String userId);

    void deleteByUserId(String userId);
}
