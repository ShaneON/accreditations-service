package com.yieldstreet.accreditation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class UserAccreditationsResponseDTO {
    private String userId;
    private Map<String, AccreditationStatusResponseDTO> accreditationStatuses;
}
