package com.yieldstreet.accreditation.dto;

import com.yieldstreet.accreditation.model.AccreditationOutcome;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccreditationFinalizationRequestDTO {

    @Valid

    @NotNull(message = "outcome is a mandatory field")
    private AccreditationOutcome outcome;
}
