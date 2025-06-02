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
    private String symbol;
    private String userId;
    private Long portfolioId;
    private Double quantity;
    private Double currentPrice;
    public Double getCurrentValue() {
        return quantity * currentPrice;
    }
}
