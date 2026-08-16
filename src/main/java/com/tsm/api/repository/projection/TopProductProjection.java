package com.tsm.api.repository.projection;

import java.math.BigDecimal;

public interface TopProductProjection {
    String getProductName();
    String getVariantName();
    Long getQuantity();
    BigDecimal getRevenue();
}