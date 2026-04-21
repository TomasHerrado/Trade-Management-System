package com.tsm.api.dto.response;
import com.tsm.api.entity.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PurchaseResponse {
    private UUID id;
    private UUID branchId;
    private String branchName;
    private UUID supplierId;
    private String supplierName;
    private String userName;
    private BigDecimal total;
    private PurchaseStatus status;
    private String note;
    private List<PurchaseItemResponse> items;
    private LocalDateTime createdAt;
}
