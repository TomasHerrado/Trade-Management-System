package com.tsm.api.service.impl;

import com.tsm.api.dto.request.BranchRequest;
import com.tsm.api.dto.response.BranchResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.*;
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
    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementRepository cashMovementRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserBranchRepository userBranchRepository;

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

    @Override
    @Transactional
    public BranchResponse activate(UUID id) {
        Branch branch = findById(id);
        branch.setStatus(BranchStatus.ACTIVE);
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Branch branch = findById(id);

        boolean hasSales = saleRepository.existsByBranchId(id);
        boolean hasPurchases = purchaseRepository.existsByBranchId(id);
        if (hasSales || hasPurchases) {
            throw new BusinessException(
                    "No se puede eliminar la sucursal porque tiene ventas o compras registradas. " +
                            "Podés desactivarla en su lugar."
            );
        }

        boolean hasDebtors = !customerAccountRepository.findDebtorsByBranchId(id).isEmpty();
        if (hasDebtors) {
            throw new BusinessException(
                    "No se puede eliminar la sucursal porque tiene clientes con saldo pendiente. " +
                            "Podés desactivarla en su lugar."
            );
        }

        List<CashRegister> registers = cashRegisterRepository.findByBranchId(id);
        for (CashRegister register : registers) {
            cashMovementRepository.deleteAll(cashMovementRepository.findByCashRegisterId(register.getId()));
        }
        cashRegisterRepository.deleteAll(registers);

        List<Stock> stocks = stockRepository.findByBranchId(id);
        for (Stock stock : stocks) {
            stockMovementRepository.deleteAll(stockMovementRepository.findByStockId(stock.getId()));
        }
        stockRepository.deleteAll(stocks);

        userBranchRepository.deleteAll(userBranchRepository.findByBranchId(id));
        customerAccountRepository.deleteAll(customerAccountRepository.findByBranchId(id));

        branchRepository.delete(branch);
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