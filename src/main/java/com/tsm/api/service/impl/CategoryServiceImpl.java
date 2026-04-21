package com.tsm.api.service.impl;
import com.tsm.api.dto.request.CategoryRequest;
import com.tsm.api.dto.response.CategoryResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.CategoryRepository;
import com.tsm.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private final CommerceServiceImpl commerceService;

    @Override
    @Transactional
    public CategoryResponse create(UUID commerceId, CategoryRequest request) {
        if (categoryRepository.existsByNameAndCommerceId(request.getName(), commerceId)) {
            throw new BusinessException("Ya existe una categoría con ese nombre");
        }
        Commerce commerce = commerceService.findById(commerceId);
        Category category = Category.builder()
                .commerce(commerce)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<CategoryResponse> getByCommerceId(UUID commerceId) {
        return categoryRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        categoryRepository.delete(findById(id));
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .commerceId(category.getCommerce().getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
