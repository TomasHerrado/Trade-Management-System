package com.tsm.api.service.impl;
import com.tsm.api.dto.request.CommerceRequest;
import com.tsm.api.dto.response.CommerceResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.CommerceRepository;
import com.tsm.api.repository.UserCommerceRepository;
import com.tsm.api.service.CommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommerceServiceImpl implements CommerceService{
    private final CommerceRepository commerceRepository;
    private final UserCommerceRepository userCommerceRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public CommerceResponse create(UUID userId, CommerceRequest request) {
        User user = userService.findById(userId);
        Commerce commerce = Commerce.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .logoUrl(request.getLogoUrl())
                .status(CommerceStatus.ACTIVE)
                .build();
        commerceRepository.save(commerce);

        // asigna al creador como OWNER
        UserCommerce userCommerce = UserCommerce.builder()
                .user(user)
                .commerce(commerce)
                .role(UserRole.OWNER)
                .build();
        userCommerceRepository.save(userCommerce);

        return toResponse(commerce);
    }

    @Override
    public CommerceResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<CommerceResponse> getByUserId(UUID userId) {
        return userCommerceRepository.findByUserId(userId).stream()
                .map(uc -> toResponse(uc.getCommerce()))
                .toList();
    }

    @Override
    @Transactional
    public CommerceResponse update(UUID id, CommerceRequest request) {
        Commerce commerce = findById(id);
        commerce.setName(request.getName());
        commerce.setType(request.getType());
        commerce.setDescription(request.getDescription());
        commerce.setAddress(request.getAddress());
        commerce.setPhone(request.getPhone());
        commerce.setLogoUrl(request.getLogoUrl());
        return toResponse(commerceRepository.save(commerce));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Commerce commerce = findById(id);
        commerce.setStatus(CommerceStatus.INACTIVE);
        commerceRepository.save(commerce);
    }

    public Commerce findById(UUID id) {
        return commerceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado"));
    }

    public CommerceResponse toResponse(Commerce commerce) {
        return CommerceResponse.builder()
                .id(commerce.getId())
                .name(commerce.getName())
                .type(commerce.getType())
                .description(commerce.getDescription())
                .address(commerce.getAddress())
                .phone(commerce.getPhone())
                .logoUrl(commerce.getLogoUrl())
                .status(commerce.getStatus())
                .createdAt(commerce.getCreatedAt())
                .build();
    }
}
