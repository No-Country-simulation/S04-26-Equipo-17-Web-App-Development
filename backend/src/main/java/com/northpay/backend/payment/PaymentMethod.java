package com.northpay.backend.payment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contractor_id", nullable = false)
    private Long contractorId;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details_json", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> detailsJson = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
