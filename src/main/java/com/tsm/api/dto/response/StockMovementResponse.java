package com.tsm.api.dto.response;
import com.tsm.api.entity.StockMovementType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockMovementResponse {
    private UUID id;
    private UUID stockId;
    private StockMovementType type;
    private Integer quantity;
    private Integer quantityAfter;
    private String note;
    private LocalDateTime createdAt;
}
