package com.tsm.api.dto.response;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BulkPriceUpdateResponse {
    private int updatedCount;
}