package com.civifix.civifix_backend.service;

import com.civifix.civifix_backend.dto.ComplaintRequestDto;
import com.civifix.civifix_backend.dto.ComplaintResponseDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.entity.ComplaintStatus;
import com.civifix.civifix_backend.entity.User;
import com.civifix.civifix_backend.repository.ComplaintRepository;
import com.civifix.civifix_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository,UserRepository userRepository){
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public Complaint createComplaint(ComplaintRequestDto requestDto, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"user not found"));
        Complaint complaint = new Complaint();
        complaint.setTitle(requestDto.getTitle());
        complaint.setDescription(requestDto.getDescription());
        complaint.setAddress(requestDto.getAddress());
        complaint.setCategory(requestDto.getCategory());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setUser(user);
        return complaintRepository.save(complaint);
    }

    public List<ComplaintResponseDto> getMyComplaints(String email) {
        return complaintRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(ComplaintResponseDto::from)
                .toList();
    }



}
