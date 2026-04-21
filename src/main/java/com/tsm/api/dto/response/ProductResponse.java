package com.tsm.api.dto.response;
import com.tsm.api.entity.ProductStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private UUID commerceId;
    private UUID categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String imageUrl;
    private ProductStatus status;
    private LocalDateTime createdAt;
}
