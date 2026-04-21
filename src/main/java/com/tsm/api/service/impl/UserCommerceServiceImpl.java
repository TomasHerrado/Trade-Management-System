package com.tsm.api.service.impl;
import com.tsm.api.dto.request.UserCommerceRequest;
import com.tsm.api.dto.response.UserCommerceResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.UserCommerceRepository;
import com.tsm.api.repository.UserRepository;
import com.tsm.api.service.UserCommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommerceServiceImpl implements UserCommerceService{
    private final UserCommerceRepository userCommerceRepository;
    private final UserRepository userRepository;
    private final CommerceServiceImpl commerceService;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public UserCommerceResponse addUser(UUID commerceId, UserCommerceRequest request) {
        Commerce commerce = commerceService.findById(commerceId);
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ese email"));

        if (userCommerceRepository.existsByUserIdAndCommerceId(user.getId(), commerceId)) {
            throw new BusinessException("El usuario ya pertenece a este comercio");
        }

        UserCommerce userCommerce = UserCommerce.builder()
                .user(user)
                .commerce(commerce)
                .role(request.getRole())
                .build();
        return toResponse(userCommerceRepository.save(userCommerce));
    }

    @Override
    public List<UserCommerceResponse> getByCommerceId(UUID commerceId) {
        return userCommerceRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeUser(UUID commerceId, UUID userId) {
        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada"));
        if (uc.getRole() == UserRole.OWNER) {
            throw new BusinessException("No se puede eliminar al propietario del comercio");
        }
        userCommerceRepository.delete(uc);
    }

    private UserCommerceResponse toResponse(UserCommerce uc) {
        return UserCommerceResponse.builder()
                .id(uc.getId())
                .user(userService.toResponse(uc.getUser()))  // necesitamos hacerlo público
                .commerce(commerceService.toResponse(uc.getCommerce()))
                .role(uc.getRole())
                .createdAt(uc.getCreatedAt())
                .build();
    }
}
