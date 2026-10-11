package com.civifix.civifix_backend.repository;

import com.civifix.civifix_backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserEmailOrderByCreatedAtDesc(String email);
}
