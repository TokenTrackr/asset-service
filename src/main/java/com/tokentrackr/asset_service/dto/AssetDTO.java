package com.tokentrackr.asset_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private UUID id;
    private String cryptoId;
    private String userId;
    private BigDecimal quantity;
    private BigDecimal currentPrice;

    public BigDecimal getCurrentValue() {
        return quantity.multiply(currentPrice);
    }
}
