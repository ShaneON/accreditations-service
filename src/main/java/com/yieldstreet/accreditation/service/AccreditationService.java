package com.yieldstreet.accreditation.service;

import com.yieldstreet.accreditation.dto.AccreditationRequestDTO;
import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.dto.AccreditationResponseDTO;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccreditationService {

    private final Map<String, Accreditation> accreditations = new HashMap<>();

    public AccreditationResponseDTO processAccreditation(AccreditationRequestDTO request) {

        String accreditationId = UUID.randomUUID().toString();

        accreditations.put(accreditationId, new Accreditation(accreditationId, request.getUserId(),
                request.getAccreditationType(), AccreditationStatus.PENDING));

        return new AccreditationResponseDTO(accreditationId);
    }

    public AccreditationResponseDTO finalizeAccreditation(String accreditationId, @NotBlank String outcome) throws APIException {
        Accreditation accreditationToFinalize = accreditations.get(accreditationId);

        if (accreditationToFinalize == null) throw new APIException("Accreditation does not exist.");

        if (!accreditationToFinalize.getStatus().equals(AccreditationStatus.PENDING)) {
            throw new APIException("Cannot update status of Accreditation that has already been finalized.");
        }

        accreditationToFinalize.setStatus(AccreditationStatus.valueOf(outcome.toUpperCase()));

        return new AccreditationResponseDTO(accreditationToFinalize.getAccreditationId());
    }
}
