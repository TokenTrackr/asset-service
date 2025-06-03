package com.tokentrackr.asset_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdateMessage {
    private String cryptoId;
    private Double price;
    private Long timestamp;
}
