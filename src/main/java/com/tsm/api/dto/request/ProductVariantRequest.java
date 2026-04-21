package com.tsm.api.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductVariantRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    private BigDecimal cost;
}
