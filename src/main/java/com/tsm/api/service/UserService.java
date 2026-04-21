package com.tsm.api.service;
import com.tsm.api.dto.request.UserLoginRequest;
import com.tsm.api.dto.request.UserRegisterRequest;
import com.tsm.api.dto.request.UserUpdateRequest;
import com.tsm.api.dto.response.AuthResponse;
import com.tsm.api.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {
    AuthResponse register(UserRegisterRequest request);
    AuthResponse login(UserLoginRequest request);
    UserResponse getById(UUID id);
    UserResponse update(UUID id, UserUpdateRequest request);
    void deactivate(UUID id);
}
