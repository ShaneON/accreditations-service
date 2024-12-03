package com.yieldstreet.accreditation.model;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccreditationRequest {
    @NotBlank
    private String userId;
    @NotNull
    private AccreditationType accreditationType;
    @NotNull
    private Document document;
}
