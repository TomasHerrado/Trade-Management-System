package com.tsm.api.service;
import com.tsm.api.dto.request.CategoryRequest;
import com.tsm.api.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;
public interface CategoryService {
    CategoryResponse create(UUID commerceId, CategoryRequest request);
    CategoryResponse getById(UUID id);
    List<CategoryResponse> getByCommerceId(UUID commerceId);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
}
