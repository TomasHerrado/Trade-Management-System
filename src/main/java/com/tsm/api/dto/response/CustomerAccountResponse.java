package com.tsm.api.dto.response;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerAccountResponse {
    private UUID id;
    private CustomerResponse customer;
    private UUID branchId;
    private String branchName;
    private BigDecimal balance;
    private LocalDateTime updatedAt;
}
