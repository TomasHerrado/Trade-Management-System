package com.tsm.api.dto.response;
import com.tsm.api.entity.CashRegisterStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CashRegisterResponse {
    private UUID id;
    private UUID branchId;
    private String branchName;
    private String openedByName;
    private String closedByName;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal currentBalance;
    private CashRegisterStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
