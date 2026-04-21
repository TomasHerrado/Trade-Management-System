package com.tsm.api.service;
import com.tsm.api.dto.request.UserCommerceRequest;
import com.tsm.api.dto.response.UserCommerceResponse;
import java.util.List;
import java.util.UUID;
public interface UserCommerceService {
    UserCommerceResponse addUser(UUID commerceId, UserCommerceRequest request);
    List<UserCommerceResponse> getByCommerceId(UUID commerceId);
    void removeUser(UUID commerceId, UUID userId);
}
