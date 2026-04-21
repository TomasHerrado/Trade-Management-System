package com.tsm.api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cash_register")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by", nullable = false)
    private User openedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by")
    private User closedBy;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal openingBalance;

    private BigDecimal closingBalance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CashRegisterStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        openedAt = LocalDateTime.now();
    }
}
