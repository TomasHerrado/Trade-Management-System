package com.tsm.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatisticsResponse {
    private BigDecimal currentMonthTotal;
    private BigDecimal previousMonthTotal;
    private BigDecimal variationPercent;

    private List<MonthlySalesPoint> salesEvolution;
    private List<TopProductPoint> topProductsByQuantity;
    private List<TopProductPoint> topProductsByRevenue;
    private List<BranchSalesPoint> salesByBranch;
    private List<PaymentTypeSalesPoint> salesByPaymentType;
    private List<LowStockPoint> lowStock;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MonthlySalesPoint {
        private int year;
        private int month;
        private String label;
        private BigDecimal total;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopProductPoint {
        private String productName;
        private String variantName;
        private Long quantity;
        private BigDecimal revenue;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BranchSalesPoint {
        private String branchName;
        private BigDecimal total;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentTypeSalesPoint {
        private String paymentType;
        private BigDecimal total;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LowStockPoint {
        private String productName;
        private String variantName;
        private String branchName;
        private Integer quantity;
        private Integer minQuantity;
    }
}