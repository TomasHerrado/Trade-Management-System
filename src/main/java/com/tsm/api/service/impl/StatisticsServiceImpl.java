package com.tsm.api.service.impl;

import com.tsm.api.dto.response.StatisticsResponse;
import com.tsm.api.entity.SaleStatus;
import com.tsm.api.repository.SaleRepository;
import com.tsm.api.repository.StockRepository;
import com.tsm.api.repository.projection.BranchSalesProjection;
import com.tsm.api.repository.projection.LowStockProjection;
import com.tsm.api.repository.projection.PaymentTypeSalesProjection;
import com.tsm.api.repository.projection.TopProductProjection;
import com.tsm.api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final int TOP_LIMIT = 5;
    private static final int EVOLUTION_MONTHS = 12;
    private static final DateTimeFormatter MONTH_LABEL_FORMATTER =
            DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "AR"));

    private final SaleRepository saleRepository;
    private final StockRepository stockRepository;

    @Override
    public StatisticsResponse getByCommerceId(UUID commerceId) {
        LocalDateTime currentMonthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1);
        LocalDateTime previousMonthStart = currentMonthStart.minusMonths(1);

        BigDecimal currentTotal = saleRepository.sumTotalByCommerceAndDateRange(
                commerceId, SaleStatus.COMPLETED, currentMonthStart, currentMonthEnd);
        BigDecimal previousTotal = saleRepository.sumTotalByCommerceAndDateRange(
                commerceId, SaleStatus.COMPLETED, previousMonthStart, currentMonthStart);

        BigDecimal variation = previousTotal.compareTo(BigDecimal.ZERO) > 0
                ? currentTotal.subtract(previousTotal)
                  .divide(previousTotal, 4, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
                  .setScale(1, RoundingMode.HALF_UP)
                : null;

        Pageable top5 = PageRequest.of(0, TOP_LIMIT);

        return StatisticsResponse.builder()
                .currentMonthTotal(currentTotal)
                .previousMonthTotal(previousTotal)
                .variationPercent(variation)
                .salesEvolution(buildEvolution(commerceId, currentMonthStart))
                .topProductsByQuantity(toTopProductList(
                        saleRepository.findTopProductsByQuantity(
                                commerceId, SaleStatus.COMPLETED, currentMonthStart, currentMonthEnd, top5)))
                .topProductsByRevenue(toTopProductList(
                        saleRepository.findTopProductsByRevenue(
                                commerceId, SaleStatus.COMPLETED, currentMonthStart, currentMonthEnd, top5)))
                .salesByBranch(toBranchList(
                        saleRepository.findSalesByBranch(
                                commerceId, SaleStatus.COMPLETED, currentMonthStart, currentMonthEnd)))
                .salesByPaymentType(toPaymentList(
                        saleRepository.findSalesByPaymentType(
                                commerceId, SaleStatus.COMPLETED, currentMonthStart, currentMonthEnd)))
                .lowStock(toLowStockList(stockRepository.findLowStockByCommerceId(commerceId)))
                .build();
    }

    private List<StatisticsResponse.MonthlySalesPoint> buildEvolution(UUID commerceId, LocalDateTime currentMonthStart) {
        List<StatisticsResponse.MonthlySalesPoint> points = new ArrayList<>();
        for (int i = EVOLUTION_MONTHS - 1; i >= 0; i--) {
            LocalDateTime from = currentMonthStart.minusMonths(i);
            LocalDateTime to = from.plusMonths(1);
            BigDecimal total = saleRepository.sumTotalByCommerceAndDateRange(
                    commerceId, SaleStatus.COMPLETED, from, to);
            points.add(StatisticsResponse.MonthlySalesPoint.builder()
                    .year(from.getYear())
                    .month(from.getMonthValue())
                    .label(capitalize(from.format(MONTH_LABEL_FORMATTER)))
                    .total(total)
                    .build());
        }
        return points;
    }

    private String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private List<StatisticsResponse.TopProductPoint> toTopProductList(List<TopProductProjection> src) {
        return src.stream().map(p -> StatisticsResponse.TopProductPoint.builder()
                .productName(p.getProductName())
                .variantName(p.getVariantName())
                .quantity(p.getQuantity())
                .revenue(p.getRevenue())
                .build()).toList();
    }

    private List<StatisticsResponse.BranchSalesPoint> toBranchList(List<BranchSalesProjection> src) {
        return src.stream().map(p -> StatisticsResponse.BranchSalesPoint.builder()
                .branchName(p.getBranchName())
                .total(p.getTotal())
                .build()).toList();
    }

    private List<StatisticsResponse.PaymentTypeSalesPoint> toPaymentList(List<PaymentTypeSalesProjection> src) {
        return src.stream().map(p -> StatisticsResponse.PaymentTypeSalesPoint.builder()
                .paymentType(p.getPaymentType().name())
                .total(p.getTotal())
                .build()).toList();
    }

    private List<StatisticsResponse.LowStockPoint> toLowStockList(List<LowStockProjection> src) {
        return src.stream().map(p -> StatisticsResponse.LowStockPoint.builder()
                .productName(p.getProductName())
                .variantName(p.getVariantName())
                .branchName(p.getBranchName())
                .quantity(p.getQuantity())
                .minQuantity(p.getMinQuantity())
                .build()).toList();
    }
}