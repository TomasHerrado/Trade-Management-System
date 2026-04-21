package com.tsm.api.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockRequest {
    @NotNull(message = "La variante es obligatoria")
    private UUID productVariantId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer quantity;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minQuantity;
}
