package com.civifix.civifix_backend.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminComplaintDto {
    private long id;
    private String userName;
    private String userEmail;
    private String title;
    private String address;
    private String priority;



}
