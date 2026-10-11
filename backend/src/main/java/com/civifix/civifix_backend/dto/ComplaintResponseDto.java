package com.civifix.civifix_backend.dto;

import com.civifix.civifix_backend.entity.Complaint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ComplaintResponseDto {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String address;
    private String status;
    private LocalDateTime createdAt;

    public static ComplaintResponseDto from(Complaint c) {
        ComplaintResponseDto dto = new ComplaintResponseDto();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setCategory(c.getCategory());
        dto.setDescription(c.getDescription());
        dto.setAddress(c.getAddress());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
