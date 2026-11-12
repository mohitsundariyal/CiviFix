package com.civifix.civifix_backend.service;

import com.civifix.civifix_backend.dto.ComplaintDetailDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.repository.ComplaintRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComplaintDetails {

    private final ComplaintRepository complaintRepository;

    public ComplaintDetails(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public ComplaintDetailDto viewDetails(Long id, String email) {

        Complaint c = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Complaint not found"));

        if (!c.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Complaint not found");
        }

        ComplaintDetailDto dto = new ComplaintDetailDto();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setCategory(c.getCategory());
        dto.setDescription(c.getDescription());
        dto.setAddress(c.getAddress());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setImagePath(c.getImagePath());
        return dto;
    }
}