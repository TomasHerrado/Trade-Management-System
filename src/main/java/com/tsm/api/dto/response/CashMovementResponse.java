package com.tsm.api.dto.response;
import com.tsm.api.entity.CashMovementType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CashMovementResponse {
    private UUID id;
    private UUID cashRegisterId;
    private CashMovementType type;
    private BigDecimal amount;
    private String description;
    private UUID referenceId;
    private LocalDateTime createdAt;
}
