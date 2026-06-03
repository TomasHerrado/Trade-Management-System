package com.tsm.api.security;

import java.util.UUID;
public interface AuthorizationService {
    void validateCommerceAccess(UUID userId, UUID commerceId);
    void validateBranchAccess(UUID userId, UUID branchId);

}
