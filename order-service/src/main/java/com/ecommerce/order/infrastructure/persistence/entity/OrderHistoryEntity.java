package com.ecommerce.order.infrastructure.persistence.entity;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus newStatus;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private String modifiedBy;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @Column(length = 1000)
    private String additionalNotes;

    @PrePersist
    protected void onCreate() {
        modifiedAt = LocalDateTime.now();
    }
}

