package com.tsm.api.service.impl;

import com.tsm.api.dto.request.CustomerRequest;
import com.tsm.api.dto.request.PaymentRequest;
import com.tsm.api.dto.response.CustomerAccountResponse;
import com.tsm.api.dto.response.CustomerResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.CustomerAccountRepository;
import com.tsm.api.repository.CustomerRepository;
import com.tsm.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final CommerceServiceImpl commerceService;
    private final BranchServiceImpl branchService;

    @Override
    @Transactional
    public CustomerResponse create(UUID commerceId, CustomerRequest request) {
        if (request.getDni() != null &&
                customerRepository.existsByDniAndCommerceId(request.getDni(), commerceId)) {
            throw new BusinessException("Ya existe un cliente con ese DNI");
        }
        Commerce commerce = commerceService.findById(commerceId);
        Customer customer = Customer.builder()
                .commerce(commerce)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dni(request.getDni())
                .status(CustomerStatus.ACTIVE)
                .build();
        return toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<CustomerResponse> getByCommerceId(UUID commerceId) {
        return customerRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = findById(id);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Customer customer = findById(id);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    @Override
    public CustomerAccountResponse getAccount(UUID customerId) {
        CustomerAccount account = customerAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta corriente no encontrada"));
        return toAccountResponse(account);
    }

    @Override
    @Transactional
    public CustomerAccountResponse registerPayment(UUID customerId, UUID branchId, PaymentRequest request) {
        CustomerAccount account = customerAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta corriente no encontrada"));
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("El pago supera la deuda del cliente");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        return toAccountResponse(customerAccountRepository.save(account));
    }

    @Override
    public List<CustomerAccountResponse> getDebtorsByBranch(UUID branchId) {
        return customerAccountRepository.findDebtorsByBranchId(branchId).stream()
                .map(this::toAccountResponse)
                .toList();
    }

    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .commerceId(customer.getCommerce().getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .dni(customer.getDni())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private CustomerAccountResponse toAccountResponse(CustomerAccount account) {
        return CustomerAccountResponse.builder()
                .id(account.getId())
                .customer(toResponse(account.getCustomer()))
                .branchId(account.getBranch().getId())
                .branchName(account.getBranch().getName())
                .balance(account.getBalance())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}