package com.tsm.api.service.impl;

import com.tsm.api.dto.request.InviteUserRequest;
import com.tsm.api.dto.response.BranchResponse;
import com.tsm.api.dto.response.UserCommerceDetailResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.UserBranchRepository;
import com.tsm.api.repository.UserCommerceRepository;
import com.tsm.api.repository.UserRepository;
import com.tsm.api.repository.BranchRepository;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final UserCommerceRepository userCommerceRepository;
    private final UserBranchRepository userBranchRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final CommerceServiceImpl commerceService;
    private final UserServiceImpl userService;
    private final BranchServiceImpl branchService;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional
    public UserCommerceDetailResponse invite(UUID commerceId, UUID inviterId,
                                             InviteUserRequest request) {
        authorizationService.validateOwner(inviterId, commerceId);

        if (request.getRole() == UserRole.OWNER) {
            throw new BusinessException("No se puede asignar el rol de propietario");
        }

        User invitedUser = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un usuario registrado con ese email"));

        if (userCommerceRepository.existsByUserIdAndCommerceId(
                invitedUser.getId(), commerceId)) {
            throw new BusinessException("El usuario ya pertenece a este comercio");
        }

        Commerce commerce = commerceService.findById(commerceId);

        UserCommerce uc = UserCommerce.builder()
                .user(invitedUser)
                .commerce(commerce)
                .role(request.getRole())
                .build();
        userCommerceRepository.save(uc);

        if (request.getRole() == UserRole.EMPLOYEE && request.getBranchId() != null) {
            Branch branch = branchService.findById(request.getBranchId());
            if (!branch.getCommerce().getId().equals(commerceId)) {
                throw new BusinessException("La sucursal no pertenece a este comercio");
            }
            UserBranch ub = UserBranch.builder()
                    .user(invitedUser)
                    .branch(branch)
                    .build();
            userBranchRepository.save(ub);
        }

        return toDetailResponse(uc);
    }

    @Override
    public List<UserCommerceDetailResponse> getTeam(UUID commerceId) {
        return userCommerceRepository.findByCommerceId(commerceId).stream()
                .map(this::toDetailResponse)
                .toList();
    }

    @Override
    @Transactional
    public void remove(UUID commerceId, UUID targetUserId, UUID requesterId) {
        authorizationService.validateOwner(requesterId, commerceId);

        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(targetUserId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario no pertenece a este comercio"));

        if (uc.getRole() == UserRole.OWNER) {
            throw new BusinessException("No se puede eliminar al propietario");
        }

        List<UUID> branchIds = userBranchRepository.findByUserId(targetUserId)
                .stream()
                .filter(ub -> ub.getBranch().getCommerce().getId().equals(commerceId))
                .map(ub -> ub.getBranch().getId())
                .toList();

        for (UUID branchId : branchIds) {
            userBranchRepository.deleteByUserIdAndBranchId(targetUserId, branchId);
        }

        userCommerceRepository.delete(uc);
    }

    @Override
    @Transactional
    public UserCommerceDetailResponse assignBranch(UUID commerceId, UUID targetUserId,
                                                   UUID branchId, UUID requesterId) {
        authorizationService.validateOwner(requesterId, commerceId);

        UserCommerce uc = userCommerceRepository
                .findByUserIdAndCommerceId(targetUserId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario no pertenece a este comercio"));

        if (uc.getRole() != UserRole.EMPLOYEE) {
            throw new BusinessException("Solo se pueden asignar sucursales a empleados");
        }

        Branch branch = branchService.findById(branchId);
        if (!branch.getCommerce().getId().equals(commerceId)) {
            throw new BusinessException("La sucursal no pertenece a este comercio");
        }

        if (userBranchRepository.existsByUserIdAndBranchId(targetUserId, branchId)) {
            throw new BusinessException("El empleado ya tiene acceso a esta sucursal");
        }

        UserBranch ub = UserBranch.builder()
                .user(uc.getUser())
                .branch(branch)
                .build();
        userBranchRepository.save(ub);

        return toDetailResponse(uc);
    }

    @Override
    @Transactional
    public void unassignBranch(UUID commerceId, UUID targetUserId,
                               UUID branchId, UUID requesterId) {
        authorizationService.validateOwner(requesterId, commerceId);
        userBranchRepository.deleteByUserIdAndBranchId(targetUserId, branchId);
    }

    private UserCommerceDetailResponse toDetailResponse(UserCommerce uc) {
        List<BranchResponse> branches = new ArrayList<>();

        if (uc.getRole() == UserRole.EMPLOYEE) {
            branches = userBranchRepository.findByUserId(uc.getUser().getId())
                    .stream()
                    .filter(ub -> ub.getBranch().getCommerce().getId()
                            .equals(uc.getCommerce().getId()))
                    .map(ub -> branchService.toResponse(ub.getBranch()))
                    .toList();
        }

        return UserCommerceDetailResponse.builder()
                .id(uc.getId())
                .user(userService.toResponse(uc.getUser()))
                .role(uc.getRole())
                .assignedBranches(branches)
                .createdAt(uc.getCreatedAt())
                .build();
    }
}