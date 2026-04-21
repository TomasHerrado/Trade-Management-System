package com.tsm.api.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryResponse {
    private UUID id;
    private UUID commerceId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
