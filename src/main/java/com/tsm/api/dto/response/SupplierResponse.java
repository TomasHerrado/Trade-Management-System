package com.tsm.api.dto.response;
import com.tsm.api.entity.SupplierStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupplierResponse {
    private UUID id;
    private UUID commerceId;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private BigDecimal debt;
    private SupplierStatus status;
    private LocalDateTime createdAt;
}
