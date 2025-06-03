package com.tokentrackr.asset_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAssetRequest {
    @NotBlank(message = "User ID cannot be blank")
    private String userid;
    @NotBlank(message = "Crypto ID cannot be blank")
    private String cryptoId;
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private double quantity;
}
