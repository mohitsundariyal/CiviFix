package com.civifix.civifix_backend.controller;


import com.civifix.civifix_backend.dto.ComplaintResponseDto;
import com.civifix.civifix_backend.service.ComplaintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ComplaintService complaintService;

    DashboardController(ComplaintService complaintService){
        this.complaintService = complaintService;

    }

    @GetMapping
    public List<ComplaintResponseDto> myComplaint(Authentication authentication){
        return complaintService.getMyComplaints(authentication.getName());

    }
}
