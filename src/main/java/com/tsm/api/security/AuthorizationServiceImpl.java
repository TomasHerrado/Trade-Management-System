package com.tsm.api.security;

import com.tsm.api.entity.Branch;
import com.tsm.api.entity.UserCommerce;
import com.tsm.api.entity.UserRole;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.exception.UnauthorizedException;
import com.tsm.api.repository.BranchRepository;
import com.tsm.api.repository.UserBranchRepository;
import com.tsm.api.repository.UserCommerceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserCommerceRepository userCommerceRepository;
    private final UserBranchRepository userBranchRepository;
    private final BranchRepository branchRepository;

    @Override
    public void validateCommerceAccess(UUID userId, UUID commerceId) {
        if (!userCommerceRepository.existsByUserIdAndCommerceId(userId, commerceId)) {
            throw new UnauthorizedException("No tenés acceso a este comercio");
        }
    }

    @Override
    public void validateBranchAccess(UUID userId, UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        UUID commerceId = branch.getCommerce().getId();

        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .orElseThrow(() -> new UnauthorizedException(
                        "No tenés acceso a esta sucursal"));

        if (uc.getRole() == UserRole.OWNER || uc.getRole() == UserRole.ADMIN) {
            return;
        }

        if (uc.getRole() == UserRole.EMPLOYEE) {
            if (!userBranchRepository.existsByUserIdAndBranchId(userId, branchId)) {
                throw new UnauthorizedException("No tenés acceso a esta sucursal");
            }
            return;
        }

        throw new UnauthorizedException("Rol no reconocido");
    }

    @Override
    public void validateOwner(UUID userId, UUID commerceId) {
        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .orElseThrow(() -> new UnauthorizedException(
                        "No tenés acceso a este comercio"));

        if (uc.getRole() != UserRole.OWNER) {
            throw new UnauthorizedException(
                    "Solo el propietario puede realizar esta acción");
        }
    }

    @Override
    public void validateOwnerOrAdmin(UUID userId, UUID commerceId) {
        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .orElseThrow(() -> new UnauthorizedException(
                        "No tenés acceso a este comercio"));

        if (uc.getRole() == UserRole.EMPLOYEE) {
            throw new UnauthorizedException(
                    "No tenés permisos para realizar esta acción");
        }
    }

    @Override
    public UserRole getUserRole(UUID userId, UUID commerceId) {
        return userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .map(UserCommerce::getRole)
                .orElseThrow(() -> new UnauthorizedException(
                        "No tenés acceso a este comercio"));
    }
}