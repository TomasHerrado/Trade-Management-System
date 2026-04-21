package com.tsm.api.service;
import com.tsm.api.dto.request.ProductVariantRequest;
import com.tsm.api.dto.response.ProductVariantResponse;
import java.util.List;
import java.util.UUID;
public interface ProductVariantService {
    ProductVariantResponse create(UUID productId, ProductVariantRequest request);
    ProductVariantResponse getById(UUID id);
    List<ProductVariantResponse> getByProductId(UUID productId);
    ProductVariantResponse update(UUID id, ProductVariantRequest request);
    void deactivate(UUID id);
}
