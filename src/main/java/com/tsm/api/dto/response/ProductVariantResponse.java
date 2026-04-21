package com.tsm.api.dto.response;
import com.tsm.api.entity.ProductStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal cost;
    private ProductStatus status;
    private LocalDateTime createdAt;
}
