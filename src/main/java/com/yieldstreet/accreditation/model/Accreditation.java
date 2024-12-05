package com.yieldstreet.accreditation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Accreditation {
    private String accreditationId;
    private String userId;
    private AccreditationType accreditationType;
    private AccreditationStatus status;
    private LocalDateTime lastUpdateTime;
}
