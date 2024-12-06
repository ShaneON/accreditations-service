package com.yieldstreet.accreditation.repository;

import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccreditationRepository {

    private final List<Accreditation> accreditations = new LinkedList<>();

    public void saveAccreditation(Accreditation accreditation) {
        accreditations.add(accreditation);
    }

    public Accreditation findAccreditation(String id) throws APIException {
        return accreditations.stream()
                .filter(accreditation -> (accreditation.getAccreditationId().equals(id)))
                .findFirst()
                .orElseThrow(() -> new APIException("No Accreditations found with id: " + id));
    }

    public List<Accreditation> findAllAccreditations() {
        return accreditations;
    }

    public List<Accreditation> findAccreditationsForUser(String userId) {
        return accreditations.stream()
                .filter(accreditation -> (accreditation.getUserId().equals(userId)))
                .collect(Collectors.toList());
    }

    public boolean doesExistPendingAccreditationForUser(String userId) {
        return accreditations.stream()
                .anyMatch(accreditation -> accreditation.getUserId().equals(userId)
                        && accreditation.getStatus() == AccreditationStatus.PENDING);
    }

}
