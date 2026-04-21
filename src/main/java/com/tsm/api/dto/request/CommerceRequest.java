package com.tsm.api.dto.request;
import com.tsm.api.entity.CommerceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommerceRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El tipo de comercio es obligatorio")
    private CommerceType type;

    private String description;
    private String address;
    private String phone;
    private String logoUrl;
}
