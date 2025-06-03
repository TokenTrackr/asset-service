package com.tokentrackr.asset_service.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }
        throw new IllegalArgumentException("Invalid authentication type");
    }

    public String getUsernameFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getClaimAsString("preferred_username");
            return username != null ? username : jwt.getClaimAsString("username");
        }
        throw new IllegalArgumentException("Invalid authentication type");
    }
}
