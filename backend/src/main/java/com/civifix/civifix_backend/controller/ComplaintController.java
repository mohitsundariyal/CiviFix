package com.civifix.civifix_backend.controller;

import com.civifix.civifix_backend.dto.ComplaintRequestDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(
            @Valid @ModelAttribute ComplaintRequestDto request,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        Complaint saved = complaintService.createComplaint(request, image, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Complaint submitted",
                        "id", saved.getId()
                ));
    }

}