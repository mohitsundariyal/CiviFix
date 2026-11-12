package com.civifix.civifix_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComplaintDetailDto {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private String imagePath;
}