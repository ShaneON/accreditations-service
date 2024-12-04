package com.yieldstreet.accreditation.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AccreditationFinalizationRequestDTO {
    @NotBlank
    private String outcome;
}
