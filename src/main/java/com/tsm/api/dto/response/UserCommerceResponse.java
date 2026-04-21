package com.tsm.api.dto.response;
import com.tsm.api.entity.UserRole;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserCommerceResponse {
    private UUID id;
    private UserResponse user;
    private CommerceResponse commerce;
    private UserRole role;
    private LocalDateTime createdAt;
}
