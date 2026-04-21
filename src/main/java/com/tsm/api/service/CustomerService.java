package com.tsm.api.service;
import com.tsm.api.dto.request.CustomerRequest;
import com.tsm.api.dto.response.CustomerAccountResponse;
import com.tsm.api.dto.response.CustomerResponse;
import com.tsm.api.dto.request.PaymentRequest;
import java.util.List;
import java.util.UUID;
public interface CustomerService {
    CustomerResponse create(UUID commerceId, CustomerRequest request);
    CustomerResponse getById(UUID id);
    List<CustomerResponse> getByCommerceId(UUID commerceId);
    CustomerResponse update(UUID id, CustomerRequest request);
    void deactivate(UUID id);
    CustomerAccountResponse getAccount(UUID customerId);
    CustomerAccountResponse registerPayment(UUID customerId, UUID branchId, PaymentRequest request);
    List<CustomerAccountResponse> getDebtorsByBranch(UUID branchId);
}
