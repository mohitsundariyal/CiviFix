package com.civifix.civifix_backend.service;

import com.civifix.civifix_backend.dto.AdminComplaintDto;
import com.civifix.civifix_backend.dto.ComplaintRequestDto;
import com.civifix.civifix_backend.dto.ComplaintResponseDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.entity.ComplaintStatus;
import com.civifix.civifix_backend.entity.Priority;
import com.civifix.civifix_backend.entity.User;
import com.civifix.civifix_backend.repository.ComplaintRepository;
import com.civifix.civifix_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintService {

    private final IssueDetectionService issueDetectionService;
    private final CloudinaryImageService cloudinaryImageService;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            UserRepository userRepository,
                            IssueDetectionService issueDetectionService,
                            CloudinaryImageService cloudinaryImageService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.issueDetectionService = issueDetectionService;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    public Complaint createComplaint(ComplaintRequestDto requestDto, MultipartFile image, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(requestDto.getTitle());
        complaint.setDescription(requestDto.getDescription());
        complaint.setAddress(requestDto.getAddress());
        complaint.setCategory(requestDto.getCategory());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.PENDING);

        // 1. check the photo (present, JPG / PNG / WEBP)
        validateImage(image);

        // 2. run the model on the photo bytes
        String issue;
        try {
            issue = issueDetectionService.detect(image.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read image");
        }

        if (issue == null) {
            complaint.setDetectedIssue("Unknown");
            complaint.setPriority(Priority.MEDIUM);
        } else {
            complaint.setDetectedIssue(issue);
            complaint.setPriority(issueDetectionService.getPriority(issue));
        }

        // 3. upload the photo to Cloudinary and keep its URL
        Map data = cloudinaryImageService.upload(image);
        Object imageUrl = data.get("secure_url");
        if (imageUrl == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image upload failed");
        }
        complaint.setImagePath((String) imageUrl);

        complaint.setUser(user);
        return complaintRepository.save(complaint);
    }

    public List<ComplaintResponseDto> getMyComplaints(String email) {
        return complaintRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(ComplaintResponseDto::from)
                .toList();
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");
        }

        String type = image.getContentType();
        if (!"image/jpeg".equals(type) && !"image/png".equals(type) && !"image/webp".equals(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, PNG or WEBP images are allowed");
        }
    }

    public Map<String, List<AdminComplaintDto>> responseAdmin() {
        List<AdminComplaintDto> high = new ArrayList<>();
        List<AdminComplaintDto> low = new ArrayList<>();
        List<AdminComplaintDto> medium = new ArrayList<>();

        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();

        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            AdminComplaintDto dto = new AdminComplaintDto();
            dto.setId(c.getId());
            dto.setCreatedAt(c.getCreatedAt());
            dto.setStatus(c.getStatus());
            dto.setUserName(c.getUser().getFullName());
            dto.setTitle(c.getTitle());
            dto.setCategory(c.getCategory());
            dto.setAddress(c.getAddress());
            dto.setUserEmail(c.getUser().getEmail());

            if (c.getPriority() == Priority.HIGH) {
                dto.setPriority("HIGH");
                high.add(dto);
            } else if (c.getPriority() == Priority.LOW) {
                dto.setPriority("LOW");
                low.add(dto);
            } else {
                dto.setPriority("MEDIUM");
                medium.add(dto);
            }
        }

        Map<String, List<AdminComplaintDto>> result = new LinkedHashMap<>();
        result.put("HIGH", high);
        result.put("LOW", low);
        result.put("MEDIUM", medium);

        return result;
    }
}