package com.yieldstreet.accreditation.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yieldstreet.accreditation.model.AccreditationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccreditationRequestDTO {

    @Valid

    @NotBlank(message = "userId is a mandatory field")
    @NotNull(message = "userId is a mandatory field")
    @NotEmpty(message = "userId is a mandatory field")
    @JsonProperty("user_id")
    private String userId;

    @NotNull(message = "accreditationType is a mandatory field")
    @JsonProperty("accreditation_type")
    private AccreditationType accreditationType;

    @Valid
    @NotNull(message = "document is a mandatory field")
    private DocumentDTO document;
}

