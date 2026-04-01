package com.pnpe.backend.service;

public interface QrCodeService {
    String generateBase64Png(String content);
}