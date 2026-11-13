package com.civifix.civifix_backend.dto;

import com.civifix.civifix_backend.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;
}