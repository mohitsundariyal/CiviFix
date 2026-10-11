package com.civifix.civifix_backend.service;

import com.civifix.civifix_backend.dto.ComplaintRequestDto;
import com.civifix.civifix_backend.dto.ComplaintResponseDto;
import com.civifix.civifix_backend.entity.Complaint;
import com.civifix.civifix_backend.entity.ComplaintStatus;
import com.civifix.civifix_backend.entity.User;
import com.civifix.civifix_backend.repository.ComplaintRepository;
import com.civifix.civifix_backend.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    //test photos folder
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository,UserRepository userRepository){
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public Complaint createComplaint(ComplaintRequestDto requestDto,MultipartFile image, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"user not found"));
        Complaint complaint = new Complaint();
        complaint.setTitle(requestDto.getTitle());
        complaint.setDescription(requestDto.getDescription());
        complaint.setAddress(requestDto.getAddress());
        complaint.setCategory(requestDto.getCategory());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setImagePath(saveImage(image));
        complaint.setUser(user);
        return complaintRepository.save(complaint);
    }

    public List<ComplaintResponseDto> getMyComplaints(String email) {
        return complaintRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(ComplaintResponseDto::from)
                .toList();
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");
        }

        String type = image.getContentType();
        String extension;
        if ("image/jpeg".equals(type)) {
            extension = ".jpg";
        } else if ("image/png".equals(type)) {
            extension = ".png";
        } else if ("image/webp".equals(type)) {
            extension = ".webp";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, PNG or WEBP images are allowed");
        }

        try {
            Path folder = Paths.get(uploadDir);
            Files.createDirectories(folder);

            String fileName = UUID.randomUUID() + extension;
            Files.copy(image.getInputStream(), folder.resolve(fileName));

            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save image");
        }
    }



}
