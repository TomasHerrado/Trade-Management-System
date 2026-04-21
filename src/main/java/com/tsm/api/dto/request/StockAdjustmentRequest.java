package com.tsm.api.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.tsm.api.entity.StockMovementType;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockAdjustmentRequest {
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private StockMovementType type;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer quantity;

    private String note;
}
