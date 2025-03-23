package dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Project: pay-stream
 * Module: dto
 * File: RejectionRequestDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class RejectionRequestDTO {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
