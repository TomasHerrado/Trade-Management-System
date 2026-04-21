package com.tsm.api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "commerce")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Commerce {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommerceType type;

    private String description;
    private String address;
    private String phone;
    private String logoUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommerceStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
