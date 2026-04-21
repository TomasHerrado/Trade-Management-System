package com.tsm.api.dto.request;
import com.tsm.api.entity.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleRequest {
    private UUID customerId;

    @NotNull(message = "El tipo de pago es obligatorio")
    private PaymentType paymentType;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<SaleItemRequest> items;

    private String note;
}
