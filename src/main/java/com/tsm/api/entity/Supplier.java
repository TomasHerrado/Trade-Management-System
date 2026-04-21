package com.tsm.api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "supplier")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private Commerce commerce;

    @Column(nullable = false)
    private String name;

    private String contactName;
    private String email;
    private String phone;
    private String address;

    // deuda pendiente con el proveedor
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal debt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SupplierStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (debt == null) debt = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
