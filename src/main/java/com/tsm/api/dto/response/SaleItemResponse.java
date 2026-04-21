package com.tsm.api.dto.response;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleItemResponse {
    private UUID id;
    private UUID productVariantId;
    private String productName;
    private String variantName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
