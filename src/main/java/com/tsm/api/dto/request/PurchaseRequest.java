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
public class PurchaseRequest {
    @NotNull(message = "El proveedor es obligatorio")
    private UUID supplierId;

    @NotNull(message = "El tipo de pago es obligatorio")
    private PaymentType paymentType;

    @NotEmpty(message = "La compra debe tener al menos un producto")
    private List<PurchaseItemRequest> items;

    private String note;
}