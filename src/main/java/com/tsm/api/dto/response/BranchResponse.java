package com.tsm.api.dto.response;
import com.tsm.api.entity.BranchStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BranchResponse {
    private UUID id;
    private UUID commerceId;
    private String commerceName;
    private String name;
    private String address;
    private String phone;
    private BranchStatus status;
    private LocalDateTime createdAt;
}
