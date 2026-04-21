package com.tsm.api.dto.response;
import com.tsm.api.entity.CustomerStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private UUID commerceId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String dni;
    private CustomerStatus status;
    private LocalDateTime createdAt;
}
