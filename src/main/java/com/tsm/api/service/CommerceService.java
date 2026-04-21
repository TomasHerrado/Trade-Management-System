package com.tsm.api.service;
import com.tsm.api.dto.request.CommerceRequest;
import com.tsm.api.dto.response.CommerceResponse;
import java.util.List;
import java.util.UUID;

public interface CommerceService {
    CommerceResponse create(UUID userId, CommerceRequest request);
    CommerceResponse getById(UUID id);
    List<CommerceResponse> getByUserId(UUID userId);
    CommerceResponse update(UUID id, CommerceRequest request);
    void deactivate(UUID id);
}
