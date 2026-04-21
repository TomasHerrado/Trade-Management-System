package com.tsm.api.service;
import com.tsm.api.dto.request.ProductRequest;
import com.tsm.api.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
public interface ProductService {
    ProductResponse create(UUID commerceId, ProductRequest request);
    ProductResponse getById(UUID id);
    List<ProductResponse> getByCommerceId(UUID commerceId);
    ProductResponse update(UUID id, ProductRequest request);
    void deactivate(UUID id);
}
