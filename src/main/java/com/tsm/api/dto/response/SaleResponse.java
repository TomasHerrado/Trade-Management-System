package com.tsm.api.dto.response;
import com.tsm.api.entity.PaymentType;
import com.tsm.api.entity.SaleStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleResponse {
    private UUID id;
    private UUID branchId;
    private String branchName;
    private UUID cashRegisterId;
    private UUID customerId;
    private String customerName;
    private String userName;
    private BigDecimal total;
    private PaymentType paymentType;
    private SaleStatus status;
    private String note;
    private List<SaleItemResponse> items;
    private LocalDateTime createdAt;
}
