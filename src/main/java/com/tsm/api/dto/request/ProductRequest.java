package com.tsm.api.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;
    private String imageUrl;
    private UUID categoryId;
}
