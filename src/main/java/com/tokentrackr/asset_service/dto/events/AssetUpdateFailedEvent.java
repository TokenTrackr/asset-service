package com.tokentrackr.asset_service.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateFailedEvent {
    private String sagaId;
    private UUID transactionId;
    private String userId;
    private String failureReason;
}
