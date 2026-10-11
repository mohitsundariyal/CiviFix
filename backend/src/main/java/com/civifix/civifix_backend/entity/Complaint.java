package com.civifix.civifix_backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="complaints")
@Getter
@Setter
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;


    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String detectedIssue;

    private String imagePath;

}
