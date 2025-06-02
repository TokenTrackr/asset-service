package com.tokentrackr.asset_service.repository;

import com.tokentrackr.asset_service.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

}
