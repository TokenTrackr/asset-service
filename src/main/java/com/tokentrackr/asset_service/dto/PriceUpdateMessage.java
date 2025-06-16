package com.tokentrackr.asset_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdateMessage {
    private String cryptoId;
    private BigDecimal price;
    private Long timestamp;
}
