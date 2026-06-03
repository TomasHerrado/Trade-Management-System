package com.tsm.api.security;

import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.exception.UnauthorizedException;
import com.tsm.api.repository.BranchRepository;
import com.tsm.api.repository.UserCommerceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService{
    private final UserCommerceRepository userCommerceRepository;
    private final BranchRepository branchRepository;

    @Override
    public void validateCommerceAccess(UUID userId, UUID commerceId) {
        if (!userCommerceRepository.existsByUserIdAndCommerceId(userId, commerceId)) {
            throw new UnauthorizedException("No tenés acceso a este comercio");
        }
    }

    @Override
    public void validateBranchAccess(UUID userId, UUID branchId) {
        UUID commerceId = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"))
                .getCommerce().getId();
        validateCommerceAccess(userId, commerceId);
    }

}
