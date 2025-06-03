package com.tokentrackr.asset_service.service.impl;

import com.tokentrackr.asset_service.service.interfaces.PriceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceCacheServiceImpl implements PriceCacheService {

    private final RedisTemplate<String, Double> redisTemplate;
    private static final String PRICE_KEY_PREFIX = "crypto:price:";
    private static final long PRICE_TTL_HOURS = 24; // Cache prices for 24 hours

    @Override
    public void cachePrice(String symbol, Double price) {
        try {
            String key = PRICE_KEY_PREFIX + symbol.toUpperCase();
            redisTemplate.opsForValue().set(key, price, PRICE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Cached price for {}: {}", symbol, price);
        } catch (Exception e) {
            log.error("Error caching price for symbol: {}", symbol, e);
        }
    }

    @Override
    public Double getPrice(String cryptoId) {
        try {
            String key = PRICE_KEY_PREFIX + cryptoId.toUpperCase();
            Double price = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved price for {}: {}", cryptoId, price);
            return price;
        } catch (Exception e) {
            log.error("Error retrieving price for symbol: {}", cryptoId, e);
            return null;
        }
    }

    @Override
    public Map<String, Double> getPrices(Set<String> symbols) {
        Map<String, Double> prices = new HashMap<>();
        if (symbols.isEmpty()) {
            return prices;
        }

        // 1. Build a List of keys (e.g. "price:BTC", "price:ETH", …)
        List<String> keys = symbols.stream()
                .map(sym -> PRICE_KEY_PREFIX + sym.toUpperCase())
                .toList();

        try {
            // 2. Do one multiGet() instead of N individual get() calls
            List<Double> values = redisTemplate.opsForValue().multiGet(keys);

            // 3. multiGet() returns a List in the same order as the keys.
            //    Now zip keys ↔ values back into a Map<symbol, price>
            for (int i = 0; i < keys.size(); i++) {
                Double price = values.get(i);
                if (price != null) {
                    // keys.get(i) is like "price:BTC" → strip the prefix to get "BTC"
                    String symbol = keys.get(i).substring(PRICE_KEY_PREFIX.length());
                    prices.put(symbol, price);
                }
            }

            log.debug("Retrieved {} prices from cache", prices.size());
        } catch (Exception e) {
            log.error("Error retrieving multiple prices", e);
        }

        return prices;
    }

    @Override
    public boolean hasPriceInCache(String symbol) {
        try {
            String key = PRICE_KEY_PREFIX + symbol.toUpperCase();
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking if price exists for symbol: {}", symbol, e);
            return false;
        }
    }

    @Override
    public void removePrice(String symbol) {
        try {
            String key = PRICE_KEY_PREFIX + symbol.toUpperCase();
            redisTemplate.delete(key);
            log.debug("Removed price from cache for symbol: {}", symbol);
        } catch (Exception e) {
            log.error("Error removing price for symbol: {}", symbol, e);
        }
    }

    @Override
    public void clearAllPrices() {
        try {
            Set<String> keys = redisTemplate.keys(PRICE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} price entries from cache", keys.size());
            }
        } catch (Exception e) {
            log.error("Error clearing all prices from cache", e);
        }
    }

    @Override
    public Set<String> getAllCachedSymbols() {
        try {
            Set<String> keys = redisTemplate.keys(PRICE_KEY_PREFIX + "*");
            if (keys != null) {
                return keys.stream()
                        .map(key -> key.substring(PRICE_KEY_PREFIX.length()))
                        .collect(java.util.stream.Collectors.toSet());
            }
        } catch (Exception e) {
            log.error("Error getting all cached symbols", e);
        }
        return Set.of();
    }
}
