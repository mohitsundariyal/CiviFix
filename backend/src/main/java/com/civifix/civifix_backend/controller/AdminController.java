package com.civifix.civifix_backend.controller;


import com.civifix.civifix_backend.dto.AdminComplaintDto;
import com.civifix.civifix_backend.dto.UpdateStatusRequest;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/complaints/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody UpdateStatusRequest request) {
        Complaint updated = complaintService.updateStatus(id, request.getStatus());
        return Map.of(
                "message", "Status updated",
                "id", updated.getId(),
                "status", updated.getStatus().name()
        );
    }
}
