package com.civifix.civifix_backend.controller;

import com.civifix.civifix_backend.dto.ComplaintRequestDto;
import com.civifix.civifix_backend.dto.ComplaintResponseDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody ComplaintRequestDto request,
            Authentication authentication) {

        Complaint saved = complaintService.createComplaint(request, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Complaint submitted",
                        "id", saved.getId()
                ));
    }

}