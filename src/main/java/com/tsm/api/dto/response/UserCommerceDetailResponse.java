package com.tsm.api.dto.response;

import com.tsm.api.entity.UserRole;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserCommerceDetailResponse {
    private UUID id;
    private UserResponse user;
    private UserRole role;
    private List<BranchResponse> assignedBranches; // poblado solo para EMPLOYEE
    private LocalDateTime createdAt;
}