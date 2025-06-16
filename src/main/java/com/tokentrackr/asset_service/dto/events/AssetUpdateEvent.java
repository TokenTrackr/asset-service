package com.tokentrackr.asset_service.dto.events;

import com.tokentrackr.asset_service.enums.TransactionType;
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
public class AssetUpdateEvent {
    private String sagaId;
    private UUID transactionId;
    private String userId;
    private String cryptoId;
    private BigDecimal quantity;
    private TransactionType transactionType;
    private boolean isCompensation;
}
