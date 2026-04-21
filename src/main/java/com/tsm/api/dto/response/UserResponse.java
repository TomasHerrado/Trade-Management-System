package com.tsm.api.dto.response;
import com.tsm.api.entity.UserStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private LocalDateTime createdAt;
}
