package com.yieldstreet.accreditation.dto;

import com.yieldstreet.accreditation.model.AccreditationStatus;
import com.yieldstreet.accreditation.model.AccreditationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccreditationStatusDTO {
    private AccreditationType accreditationType;
    private AccreditationStatus accreditationStatus;
}
