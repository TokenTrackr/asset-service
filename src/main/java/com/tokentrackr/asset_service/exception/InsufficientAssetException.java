package com.tokentrackr.asset_service.exception;

public class InsufficientAssetException extends RuntimeException {
    public InsufficientAssetException(String message) {
        super(message);
    }
}
