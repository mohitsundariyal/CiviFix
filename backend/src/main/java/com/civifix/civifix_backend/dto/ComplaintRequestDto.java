package com.civifix.civifix_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ComplaintRequestDto {
    @NotBlank(message = "title is requeired")
    private String title;

    @NotBlank(message = "category is requeired")
    private String category;

    @NotBlank(message = "description is requeired")
    private String description;

    @NotBlank(message = "address is requeired")
    private String address;


}
