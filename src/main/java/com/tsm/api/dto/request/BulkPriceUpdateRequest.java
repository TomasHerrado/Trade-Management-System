package com.tsm.api.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BulkPriceUpdateRequest {
    @NotNull(message = "El proveedor es obligatorio")
    private UUID supplierId;

    @NotNull(message = "El porcentaje es obligatorio")
    private BigDecimal percentage; // ej: 5 para +5%, -10 para -10%

    @NotNull(message = "Indicá qué actualizar")
    private PriceUpdateTarget applyTo;
}