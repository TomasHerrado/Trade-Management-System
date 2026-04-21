package com.tsm.api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StockMovementType type;

    @Column(nullable = false)
    private Integer quantity;

    // cantidad resultante luego del movimiento
    @Column(nullable = false)
    private Integer quantityAfter;

    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
