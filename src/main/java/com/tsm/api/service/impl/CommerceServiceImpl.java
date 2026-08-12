package com.tsm.api.service.impl;

import com.tsm.api.dto.request.CommerceRequest;
import com.tsm.api.dto.response.CommerceResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.*;
import com.tsm.api.service.CommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommerceServiceImpl implements CommerceService{
    private final CommerceRepository commerceRepository;
    private final UserCommerceRepository userCommerceRepository;
    private final UserServiceImpl userService;
    private final BranchRepository branchRepository;
    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementRepository cashMovementRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserBranchRepository userBranchRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public CommerceResponse create(UUID userId, CommerceRequest request) {
        User user = userService.findById(userId);
        Commerce commerce = Commerce.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .logoUrl(request.getLogoUrl())
                .status(CommerceStatus.ACTIVE)
                .build();
        commerceRepository.save(commerce);

        // asigna al creador como OWNER
        UserCommerce userCommerce = UserCommerce.builder()
                .user(user)
                .commerce(commerce)
                .role(UserRole.OWNER)
                .build();
        userCommerceRepository.save(userCommerce);

        return toResponse(commerce);
    }

    @Override
    public CommerceResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<CommerceResponse> getByUserId(UUID userId) {
        return userCommerceRepository.findByUserId(userId).stream()
                .map(uc -> toResponse(uc.getCommerce()))
                .toList();
    }

    @Override
    @Transactional
    public CommerceResponse update(UUID id, CommerceRequest request) {
        Commerce commerce = findById(id);
        commerce.setName(request.getName());
        commerce.setType(request.getType());
        commerce.setDescription(request.getDescription());
        commerce.setAddress(request.getAddress());
        commerce.setPhone(request.getPhone());
        commerce.setLogoUrl(request.getLogoUrl());
        return toResponse(commerceRepository.save(commerce));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Commerce commerce = findById(id);
        commerce.setStatus(CommerceStatus.INACTIVE);
        commerceRepository.save(commerce);
    }

    @Override
    @Transactional
    public CommerceResponse activate(UUID id) {
        Commerce commerce = findById(id);
        commerce.setStatus(CommerceStatus.ACTIVE);
        return toResponse(commerceRepository.save(commerce));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Commerce commerce = findById(id);
        List<Branch> branches = branchRepository.findByCommerceId(id);

        // Validaciones: no se puede eliminar si hay historial financiero
        for (Branch branch : branches) {
            boolean hasSales = saleRepository.existsByBranchId(branch.getId());
            boolean hasPurchases = purchaseRepository.existsByBranchId(branch.getId());
            if (hasSales || hasPurchases) {
                throw new BusinessException(
                        "No se puede eliminar el comercio porque la sucursal \"" + branch.getName() +
                                "\" tiene ventas o compras registradas. Podés desactivarlo en su lugar."
                );
            }
            boolean hasDebtors = !customerAccountRepository.findDebtorsByBranchId(branch.getId()).isEmpty();
            if (hasDebtors) {
                throw new BusinessException(
                        "No se puede eliminar el comercio porque la sucursal \"" + branch.getName() +
                                "\" tiene clientes con saldo pendiente. Podés desactivarlo en su lugar."
                );
            }
        }

        boolean hasSupplierDebt = !supplierRepository.findWithDebtByCommerceId(id).isEmpty();
        if (hasSupplierDebt) {
            throw new BusinessException(
                    "No se puede eliminar el comercio porque tiene proveedores con deuda pendiente. " +
                            "Podés desactivarlo en su lugar."
            );
        }

        // Cascada por sucursal: caja, stock, asignaciones de usuarios, cuentas corrientes
        for (Branch branch : branches) {
            List<CashRegister> registers = cashRegisterRepository.findByBranchId(branch.getId());
            for (CashRegister register : registers) {
                cashMovementRepository.deleteAll(cashMovementRepository.findByCashRegisterId(register.getId()));
            }
            cashRegisterRepository.deleteAll(registers);

            List<Stock> stocks = stockRepository.findByBranchId(branch.getId());
            for (Stock stock : stocks) {
                stockMovementRepository.deleteAll(stockMovementRepository.findByStockId(stock.getId()));
            }
            stockRepository.deleteAll(stocks);

            userBranchRepository.deleteAll(userBranchRepository.findByBranchId(branch.getId()));
            customerAccountRepository.deleteAll(customerAccountRepository.findByBranchId(branch.getId()));
        }
        branchRepository.deleteAll(branches);

        // Productos y variantes (ya se validó que no tienen ventas/compras)
        List<Product> products = productRepository.findByCommerceId(id);
        for (Product product : products) {
            productVariantRepository.deleteAll(productVariantRepository.findByProductId(product.getId()));
        }
        productRepository.deleteAll(products);

        categoryRepository.deleteAll(categoryRepository.findByCommerceId(id));
        customerRepository.deleteAll(customerRepository.findByCommerceId(id));
        supplierRepository.deleteAll(supplierRepository.findByCommerceId(id));
        userCommerceRepository.deleteAll(userCommerceRepository.findByCommerceId(id));

        commerceRepository.delete(commerce);
    }

    public Commerce findById(UUID id) {
        return commerceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado"));
    }

    public CommerceResponse toResponse(Commerce commerce) {
        return CommerceResponse.builder()
                .id(commerce.getId())
                .name(commerce.getName())
                .type(commerce.getType())
                .description(commerce.getDescription())
                .address(commerce.getAddress())
                .phone(commerce.getPhone())
                .logoUrl(commerce.getLogoUrl())
                .status(commerce.getStatus())
                .createdAt(commerce.getCreatedAt())
                .build();
    }
}