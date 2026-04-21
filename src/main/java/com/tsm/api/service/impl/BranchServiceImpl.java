package com.tsm.api.service.impl;
import com.tsm.api.dto.request.BranchRequest;
import com.tsm.api.dto.response.BranchResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.BranchRepository;
import com.tsm.api.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService{
    private final BranchRepository branchRepository;
    private final CommerceServiceImpl commerceService;

    @Override
    @Transactional
    public BranchResponse create(UUID commerceId, BranchRequest request) {
        Commerce commerce = commerceService.findById(commerceId);
        Branch branch = Branch.builder()
                .commerce(commerce)
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .status(BranchStatus.ACTIVE)
                .build();
        return toResponse(branchRepository.save(branch));
    }

    @Override
    public BranchResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<BranchResponse> getByCommerceId(UUID commerceId) {
        return branchRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BranchResponse update(UUID id, BranchRequest request) {
        Branch branch = findById(id);
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Branch branch = findById(id);
        branch.setStatus(BranchStatus.INACTIVE);
        branchRepository.save(branch);
    }

    public Branch findById(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
    }

    public BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .commerceId(branch.getCommerce().getId())
                .commerceName(branch.getCommerce().getName())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .status(branch.getStatus())
                .createdAt(branch.getCreatedAt())
                .build();
    }
}
