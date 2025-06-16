package com.tokentrackr.asset_service.service.interfaces;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface PriceCacheService {

    /**
     * Store price for a symbol in Redis cache
     */
    void cachePrice(String symbol, BigDecimal price);

    /**
     * Get price for a symbol from Redis cache
     */
    BigDecimal getPrice(String symbol);

    /**
     * Get prices for multiple symbols
     */
    Map<String, BigDecimal> getPrices(Set<String> symbols);

    /**
     * Check if price exists in cache
     */
    boolean hasPriceInCache(String symbol);

    /**
     * Remove price from cache
     */
    void removePrice(String symbol);

    /**
     * Clear all cached prices
     */
    void clearAllPrices();

    /**
     * Get all cached symbols
     */
    Set<String> getAllCachedSymbols();
}
