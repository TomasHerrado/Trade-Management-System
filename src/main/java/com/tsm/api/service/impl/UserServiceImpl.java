package com.tsm.api.service.impl;
import com.tsm.api.dto.request.UserLoginRequest;
import com.tsm.api.dto.request.UserRegisterRequest;
import com.tsm.api.dto.request.UserUpdateRequest;
import com.tsm.api.dto.response.AuthResponse;
import com.tsm.api.dto.response.UserResponse;
import com.tsm.api.entity.User;
import com.tsm.api.entity.UserStatus;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.UserRepository;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("El usuario no está activo");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = findById(id);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = findById(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        User user = findById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
