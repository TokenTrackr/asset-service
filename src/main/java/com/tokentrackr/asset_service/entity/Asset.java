package com.tokentrackr.asset_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "assets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "crypto_id", nullable = false)
    private String cryptoId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO; // default to 0

    @Transient
    private Double currentPrice = 0.0;

    public BigDecimal getCurrentValue() {
        // Convert currentPrice to BigDecimal safely
        BigDecimal price = currentPrice != null ? BigDecimal.valueOf(currentPrice) : BigDecimal.ZERO;
        return quantity.multiply(price);
    }
}
