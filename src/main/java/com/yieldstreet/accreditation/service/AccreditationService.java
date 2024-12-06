package com.yieldstreet.accreditation.service;

import com.yieldstreet.accreditation.dto.AccreditationRequestDTO;
import com.yieldstreet.accreditation.dto.AccreditationStatusResponseDTO;
import com.yieldstreet.accreditation.dto.UserAccreditationsResponseDTO;
import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.dto.AccreditationResponseDTO;
import com.yieldstreet.accreditation.model.AccreditationOutcome;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import com.yieldstreet.accreditation.repository.AccreditationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccreditationService {

    @Autowired
    AccreditationRepository accreditationRepository;

    public AccreditationResponseDTO processAccreditation(AccreditationRequestDTO request) throws APIException {
        if (accreditationRepository.doesExistPendingAccreditationForUser(request.getUserId()))
            throw new APIException("User already has a pending Accreditation.");

        String accreditationId = UUID.randomUUID().toString();
        Accreditation pendingAccreditation = new Accreditation(accreditationId, request.getUserId(), request.getAccreditationType(), AccreditationStatus.PENDING, LocalDateTime.now());

        accreditationRepository.saveAccreditation(pendingAccreditation);

        return new AccreditationResponseDTO(accreditationId);
    }

    public AccreditationResponseDTO finalizeAccreditation(String accreditationId, AccreditationOutcome outcome) throws APIException {
        Accreditation accreditationToFinalize = accreditationRepository.findAccreditation(accreditationId);

        if (accreditationToFinalize == null) throw new APIException("Accreditation does not exist.");

        validateFinalizeRequest(accreditationToFinalize, outcome);
        accreditationToFinalize.setStatus(AccreditationStatus.valueOf(outcome.toString()));

        return new AccreditationResponseDTO(accreditationToFinalize.getAccreditationId());
    }

    public UserAccreditationsResponseDTO findAllAccreditationsForUser(String userId) throws APIException {
        List<Accreditation> accreditations = accreditationRepository.findAccreditationsForUser(userId);

        if (accreditations.isEmpty()) throw new APIException("No accreditations found for userId: " + userId);

        Map<String, AccreditationStatusResponseDTO> accreditationStatusMap = accreditations.stream()
                .collect(Collectors.toMap(
                        Accreditation::getAccreditationId,
                        accreditation -> new AccreditationStatusResponseDTO(
                                accreditation.getAccreditationType(),
                                accreditation.getStatus()
                        )
                ));

        return new UserAccreditationsResponseDTO(userId, accreditationStatusMap);
    }

    private void validateFinalizeRequest(Accreditation accreditation, AccreditationOutcome outcome) throws APIException {
        switch (accreditation.getStatus()) {
            case PENDING:
                accreditation.setStatus(AccreditationStatus.valueOf(outcome.toString()));
                accreditation.setLastUpdateTime(LocalDateTime.now());
                break;
            case CONFIRMED:
                if (outcome == AccreditationOutcome.EXPIRED) {
                    accreditation.setStatus(AccreditationStatus.valueOf(outcome.toString()));
                    accreditation.setLastUpdateTime(LocalDateTime.now());
                }
                break;
            case EXPIRED:
                throw new APIException("Accreditation is already in EXPIRED state.");
            case FAILED:
                throw new APIException("Accreditation is already in FAILED state.");
        }
    }
}
