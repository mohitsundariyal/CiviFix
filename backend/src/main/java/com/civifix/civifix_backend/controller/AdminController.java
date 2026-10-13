package com.civifix.civifix_backend.controller;


import com.civifix.civifix_backend.dto.AdminComplaintDto;
import com.civifix.civifix_backend.service.ComplaintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final ComplaintService complaintService;

    AdminController(ComplaintService complaintService){
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public Map<String, List<AdminComplaintDto>> response(){
        Map<String, List<AdminComplaintDto>> result = complaintService.responseAdmin();
        return result;

    }
}
