package com.northpay.backend.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    Optional<Contractor> findByEmail(String email);
}