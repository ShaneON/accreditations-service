package com.yieldstreet.accreditation.service;

import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.model.AccreditationRequest;
import com.yieldstreet.accreditation.model.AccreditationResponse;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccreditationService {

    private final Map<String, Accreditation> accreditations = new HashMap<>();

    public AccreditationResponse processAccreditation(AccreditationRequest request) {

        String accreditationId = UUID.randomUUID().toString();

        accreditations.put(accreditationId, new Accreditation(accreditationId, request.getUserId(),
                request.getAccreditationType(), AccreditationStatus.PENDING));

        return new AccreditationResponse(accreditationId);
    }
}
