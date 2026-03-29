package com.pnpe.backend.service;

import com.pnpe.backend.dto.AuthRequest;
import com.pnpe.backend.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
