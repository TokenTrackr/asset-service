package com.tokentrackr.asset_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crypto_id", nullable = false)
    private String cryptoId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private double quantity;

    // Current price is transient - not stored in DB
    @Transient
    private Double currentPrice = 0.0;

    public Double getCurrentValue() {
        return quantity * (currentPrice != null ? currentPrice : 0.0);
    }
}
