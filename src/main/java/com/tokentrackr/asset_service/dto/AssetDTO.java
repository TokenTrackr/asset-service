package com.tokentrackr.asset_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private Long id;
    private String cryptoId;
    private String userId;
    private double quantity;
    private double currentPrice;

    public double getCurrentValue() {

        return quantity * currentPrice;
    }
}
