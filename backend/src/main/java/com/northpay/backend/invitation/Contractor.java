package com.northpay.backend.invitation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contractors")
@Builder
public class Contractor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName = "";

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName = "";

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName = "";

    @Column(name = "preferred_name", length = 80)
    private String preferredName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "id_document_number", length = 50)
    private String idDocumentNumber;

    @Column(name = "tax_regime", length = 80)
    private String taxRegime;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "country_iso", length = 2)
    private String countryIso;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
