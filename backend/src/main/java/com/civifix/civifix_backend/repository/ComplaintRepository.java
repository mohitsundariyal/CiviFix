package com.civifix.civifix_backend.repository;

import com.civifix.civifix_backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
