package com.yieldstreet.accreditation.service;

import com.yieldstreet.accreditation.dto.AccreditationRequestDTO;
import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.dto.AccreditationResponseDTO;
import com.yieldstreet.accreditation.model.AccreditationOutcome;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccreditationService {

    private final Map<String, Accreditation> accreditations = new HashMap<>();

    public AccreditationResponseDTO processAccreditation(AccreditationRequestDTO request) throws APIException {

        if (hasPendingAccreditation(request.getUserId())) throw new APIException("User already has a pending Accreditation.");

        String accreditationId = UUID.randomUUID().toString();

        accreditations.put(accreditationId, new Accreditation(accreditationId, request.getUserId(),
                request.getAccreditationType(), AccreditationStatus.PENDING));

        return new AccreditationResponseDTO(accreditationId);
    }

    public AccreditationResponseDTO finalizeAccreditation(String accreditationId, AccreditationOutcome outcome) throws APIException {
        Accreditation accreditationToFinalize = accreditations.get(accreditationId);

        if (accreditationToFinalize == null) throw new APIException("Accreditation does not exist.");

        validateFinalizeRequest(accreditationToFinalize, outcome);

        accreditationToFinalize.setStatus(AccreditationStatus.valueOf(outcome.toString()));

        return new AccreditationResponseDTO(accreditationToFinalize.getAccreditationId());
    }

    private void validateFinalizeRequest(Accreditation accreditation, AccreditationOutcome outcome) throws APIException {
        switch (accreditation.getStatus()) {
            case PENDING:
                accreditation.setStatus(AccreditationStatus.valueOf(outcome.toString()));
                break;
            case CONFIRMED:
                if (outcome == AccreditationOutcome.EXPIRED) accreditation.setStatus(AccreditationStatus.valueOf(outcome.toString()));
                break;
            case EXPIRED:
                throw new APIException("Accreditation is already in EXPIRED state.");
            case FAILED:
                throw new APIException("Accreditation is already in FAILED state.");
        }
    }

    private boolean hasPendingAccreditation(String userId) {
        for (Accreditation a : accreditations.values()) {
            if (a.getUserId().equals(userId) && a.getStatus() == AccreditationStatus.PENDING) {
                return true;
            }
        }
        return false;
    }
}
