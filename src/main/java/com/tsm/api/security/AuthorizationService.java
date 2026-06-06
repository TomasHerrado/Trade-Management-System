package com.tsm.api.security;

import com.tsm.api.entity.UserRole;
import java.util.UUID;

public interface AuthorizationService {

    void validateCommerceAccess(UUID userId, UUID commerceId);

    void validateBranchAccess(UUID userId, UUID branchId);

    void validateOwner(UUID userId, UUID commerceId);

    void validateOwnerOrAdmin(UUID userId, UUID commerceId);

    UserRole getUserRole(UUID userId, UUID commerceId);
}