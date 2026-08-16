package com.tsm.api.repository.projection;

import java.math.BigDecimal;

public interface BranchSalesProjection {
    String getBranchName();
    BigDecimal getTotal();
}