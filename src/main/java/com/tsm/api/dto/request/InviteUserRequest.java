package com.tsm.api.dto.request;

import com.tsm.api.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InviteUserRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String userEmail;

    @NotNull(message = "El rol es obligatorio")
    private UserRole role;

    private UUID branchId;
}