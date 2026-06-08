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

    /**
     * Verifica que el usuario tenga acceso al comercio (cualquier rol).
     * OWNER, ADMIN y EMPLOYEE con UserCommerce en ese comercio pasan.
     */
    @Override
    public void validateCommerceAccess(UUID userId, UUID commerceId) {
        if (!userCommerceRepository.existsByUserIdAndCommerceId(userId, commerceId)) {
            throw new UnauthorizedException("No tenés acceso a este comercio");
        }
    }

    /**
     * Verifica acceso a una sucursal según el rol:
     * - OWNER: acceso a todas las sucursales de sus comercios.
     * - ADMIN: acceso a todas las sucursales del comercio que administra.
     * - EMPLOYEE: solo las sucursales asignadas explícitamente en UserBranch.
     *
     * También valida que la sucursal pertenezca a un comercio del usuario,
     * impidiendo que un ADMIN de Comercio A acceda a sucursales de Comercio B.
     */
    @Override
    public void validateBranchAccess(UUID userId, UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        UUID commerceId = branch.getCommerce().getId();

        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .orElseThrow(() -> new UnauthorizedException(
                        "No tenés acceso a esta sucursal"));

        switch (uc.getRole()) {
            case OWNER, ADMIN -> {
                // Tienen acceso a todas las sucursales del comercio asignado.
                // La verificación de que el comercio es correcto ya ocurrió arriba.
            }
            case EMPLOYEE -> {
                if (!userBranchRepository.existsByUserIdAndBranchId(userId, branchId)) {
                    throw new UnauthorizedException("No tenés acceso a esta sucursal");
                }
            }
            default -> throw new UnauthorizedException("Rol no reconocido");
        }
    }

    /**
     * Solo OWNER puede ejecutar la acción.
     */
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

    /**
     * OWNER o ADMIN pueden ejecutar la acción. EMPLOYEE no.
     */
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

    /**
     * Valida acceso a nivel de sucursal pero solo para OWNER o ADMIN.
     * Usado en endpoints donde EMPLOYEE no tiene permitido operar.
     */
    public void validateOwnerOrAdminForBranch(UUID userId, UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        UUID commerceId = branch.getCommerce().getId();
        validateOwnerOrAdmin(userId, commerceId);
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