package com.tsm.api.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockResponse {
    private UUID id;
    private UUID branchId;
    private String branchName;
    private UUID productVariantId;
    private String productName;
    private String variantName;
    private String sku;
    private Integer quantity;
    private Integer minQuantity;
    private boolean lowStock;
    private LocalDateTime updatedAt;
}
