package com.tsm.api.dto.request;
import com.tsm.api.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserCommerceRequest {
    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email no es válido")
    private String userEmail;

    @NotNull(message = "El rol es obligatorio")
    private UserRole role;
}
