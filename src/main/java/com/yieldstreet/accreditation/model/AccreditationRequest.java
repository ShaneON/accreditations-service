package com.yieldstreet.accreditation.model;

import lombok.Data;

@Data
public class AccreditationRequest {
    private String userId;

    private String accreditationType;

    private Document document;
}
