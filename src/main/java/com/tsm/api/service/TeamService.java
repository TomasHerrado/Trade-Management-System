package com.tsm.api.service;

import com.tsm.api.dto.request.InviteUserRequest;
import com.tsm.api.dto.response.UserCommerceDetailResponse;
import java.util.List;
import java.util.UUID;

public interface TeamService {
    UserCommerceDetailResponse invite(UUID commerceId, UUID inviterId, InviteUserRequest request);
    List<UserCommerceDetailResponse> getTeam(UUID commerceId);
    void remove(UUID commerceId, UUID targetUserId, UUID requesterId);
    UserCommerceDetailResponse assignBranch(UUID commerceId, UUID targetUserId, UUID branchId, UUID requesterId);
    void unassignBranch(UUID commerceId, UUID targetUserId, UUID branchId, UUID requesterId);
}