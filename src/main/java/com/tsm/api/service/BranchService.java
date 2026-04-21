package com.tsm.api.service;
import com.tsm.api.dto.request.BranchRequest;
import com.tsm.api.dto.response.BranchResponse;
import java.util.List;
import java.util.UUID;
public interface BranchService {
    BranchResponse create(UUID commerceId, BranchRequest request);
    BranchResponse getById(UUID id);
    List<BranchResponse> getByCommerceId(UUID commerceId);
    BranchResponse update(UUID id, BranchRequest request);
    void deactivate(UUID id);
}
