package com.tsm.api.dto.response;
import com.tsm.api.entity.CommerceStatus;
import com.tsm.api.entity.CommerceType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommerceResponse {
    private UUID id;
    private String name;
    private CommerceType type;
    private String description;
    private String address;
    private String phone;
    private String logoUrl;
    private CommerceStatus status;
    private LocalDateTime createdAt;
}
