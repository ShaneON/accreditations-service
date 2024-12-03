package com.yieldstreet.accreditation.model;

import lombok.Data;

@Data
public class AccreditationResponse {

    private String accreditationId;

    public AccreditationResponse(String accreditationId) {
        this.accreditationId = accreditationId;
    }


}