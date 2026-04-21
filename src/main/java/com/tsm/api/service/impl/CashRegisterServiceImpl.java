package com.tsm.api.service.impl;

import com.tsm.api.dto.request.CashRegisterOpenRequest;
import com.tsm.api.dto.response.CashMovementResponse;
import com.tsm.api.dto.response.CashRegisterResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.CashMovementRepository;
import com.tsm.api.repository.CashRegisterRepository;
import com.tsm.api.service.CashRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashRegisterServiceImpl implements CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementRepository cashMovementRepository;
    private final BranchServiceImpl branchService;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public CashRegisterResponse open(UUID branchId, UUID userId, CashRegisterOpenRequest request) {
        if (cashRegisterRepository.existsByBranchIdAndStatus(branchId, CashRegisterStatus.OPEN)) {
            throw new BusinessException("Ya existe una caja abierta en esta sucursal");
        }
        Branch branch = branchService.findById(branchId);
        User user = userService.findById(userId);

        CashRegister cashRegister = CashRegister.builder()
                .branch(branch)
                .openedBy(user)
                .openingBalance(request.getOpeningBalance())
                .status(CashRegisterStatus.OPEN)
                .build();
        cashRegisterRepository.save(cashRegister);

        // registra movimiento de apertura
        CashMovement movement = CashMovement.builder()
                .cashRegister(cashRegister)
                .type(CashMovementType.OPENING)
                .amount(request.getOpeningBalance())
                .description("Apertura de caja")
                .build();
        cashMovementRepository.save(movement);

        return toResponse(cashRegister);
    }

    @Override
    @Transactional
    public CashRegisterResponse close(UUID branchId, UUID userId) {
        CashRegister cashRegister = cashRegisterRepository
                .findByBranchIdAndStatus(branchId, CashRegisterStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("No hay caja abierta en esta sucursal"));

        User user = userService.findById(userId);
        java.math.BigDecimal total = cashMovementRepository.sumByCashRegisterId(cashRegister.getId());

        cashRegister.setClosedBy(user);
        cashRegister.setClosingBalance(total);
        cashRegister.setStatus(CashRegisterStatus.CLOSED);
        cashRegister.setClosedAt(LocalDateTime.now());

        CashMovement movement = CashMovement.builder()
                .cashRegister(cashRegister)
                .type(CashMovementType.CLOSING)
                .amount(total)
                .description("Cierre de caja")
                .build();
        cashMovementRepository.save(movement);

        return toResponse(cashRegisterRepository.save(cashRegister));
    }

    @Override
    public CashRegisterResponse getOpenByBranchId(UUID branchId) {
        CashRegister cashRegister = cashRegisterRepository
                .findByBranchIdAndStatus(branchId, CashRegisterStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("No hay caja abierta"));
        return toResponse(cashRegister);
    }

    @Override
    public List<CashRegisterResponse> getHistoryByBranchId(UUID branchId) {
        return cashRegisterRepository.findByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CashMovementResponse> getMovements(UUID cashRegisterId) {
        return cashMovementRepository.findByCashRegisterId(cashRegisterId).stream()
                .map(this::toMovementResponse)
                .toList();
    }

    public CashRegister findOpenByBranchId(UUID branchId) {
        return cashRegisterRepository
                .findByBranchIdAndStatus(branchId, CashRegisterStatus.OPEN)
                .orElseThrow(() -> new BusinessException("No hay caja abierta en esta sucursal"));
    }

    public void registerMovement(CashRegister cashRegister, CashMovementType type,
                                 java.math.BigDecimal amount, String description, UUID referenceId) {
        CashMovement movement = CashMovement.builder()
                .cashRegister(cashRegister)
                .type(type)
                .amount(amount)
                .description(description)
                .referenceId(referenceId)
                .build();
        cashMovementRepository.save(movement);
    }

    public CashRegisterResponse toResponse(CashRegister cashRegister) {
        java.math.BigDecimal current = cashMovementRepository
                .sumByCashRegisterId(cashRegister.getId());
        return CashRegisterResponse.builder()
                .id(cashRegister.getId())
                .branchId(cashRegister.getBranch().getId())
                .branchName(cashRegister.getBranch().getName())
                .openedByName(cashRegister.getOpenedBy().getFirstName()
                        + " " + cashRegister.getOpenedBy().getLastName())
                .closedByName(cashRegister.getClosedBy() != null
                        ? cashRegister.getClosedBy().getFirstName()
                          + " " + cashRegister.getClosedBy().getLastName()
                        : null)
                .openingBalance(cashRegister.getOpeningBalance())
                .closingBalance(cashRegister.getClosingBalance())
                .currentBalance(current)
                .status(cashRegister.getStatus())
                .openedAt(cashRegister.getOpenedAt())
                .closedAt(cashRegister.getClosedAt())
                .build();
    }

    private CashMovementResponse toMovementResponse(CashMovement movement) {
        return CashMovementResponse.builder()
                .id(movement.getId())
                .cashRegisterId(movement.getCashRegister().getId())
                .type(movement.getType())
                .amount(movement.getAmount())
                .description(movement.getDescription())
                .referenceId(movement.getReferenceId())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}