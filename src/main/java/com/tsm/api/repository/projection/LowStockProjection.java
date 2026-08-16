package com.tsm.api.repository.projection;

public interface LowStockProjection {
    String getProductName();
    String getVariantName();
    String getBranchName();
    Integer getQuantity();
    Integer getMinQuantity();
}