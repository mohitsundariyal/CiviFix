package com.civifix.civifix_backend.dto;


import com.civifix.civifix_backend.entity.ComplaintStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminComplaintDto {
    private long id;
    private String userName;
    private String userEmail;
    private String title;
    private String address;
    private String priority;
    private LocalDateTime createdAt;
    private ComplaintStatus status;
    private String category;

}
