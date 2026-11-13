package com.civifix.civifix_backend.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageServiceImplementaion implements CloudinaryImageService{
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map upload(MultipartFile file){
        try{
            Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
            return data;

        }catch (IOException e){
            throw new RuntimeException("image uploading fails");

        }
    }
}
