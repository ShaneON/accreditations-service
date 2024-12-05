package com.yieldstreet.accreditation.unit.service;

import com.yieldstreet.accreditation.dto.*;
import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.model.AccreditationOutcome;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import com.yieldstreet.accreditation.model.AccreditationType;
import com.yieldstreet.accreditation.repository.AccreditationRepository;
import com.yieldstreet.accreditation.service.AccreditationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccreditationServiceTest {

    @Mock
    private AccreditationRepository accreditationRepository;

    @InjectMocks
    private AccreditationService accreditationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAccreditation_Success() throws APIException {
        AccreditationRequestDTO request = new AccreditationRequestDTO(
                "user123",
                AccreditationType.BY_INCOME,
                new DocumentDTO("2015.pdf", "application/pdf", "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg==")
        );

        when(accreditationRepository.doesExistPendingAccreditationForUser(request.getUserId())).thenReturn(false);

        AccreditationResponseDTO response = accreditationService.processAccreditation(request);

        assertNotNull(response.getAccreditationId());
        verify(accreditationRepository, times(1)).doesExistPendingAccreditationForUser(request.getUserId());
        verify(accreditationRepository, times(1)).saveAccreditation(any(Accreditation.class));
    }

    @Test
    void testProcessAccreditation_UserHasPendingAccreditation() {
        AccreditationRequestDTO request = new AccreditationRequestDTO(
                "user123",
                AccreditationType.BY_INCOME,
                new DocumentDTO("2015.pdf", "application/pdf", "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg==")
        );

        when(accreditationRepository.doesExistPendingAccreditationForUser(request.getUserId())).thenReturn(true);

        APIException exception = assertThrows(APIException.class, () -> {
            accreditationService.processAccreditation(request);
        });

        assertEquals("User already has a pending Accreditation.", exception.getMessage());
        verify(accreditationRepository, times(1)).doesExistPendingAccreditationForUser(request.getUserId());
        verify(accreditationRepository, never()).saveAccreditation(any(Accreditation.class));
    }

    @Test
    void testFindAllAccreditationsForUser() {
        // Arrange
        String userId = "user123";
        List<Accreditation> accreditations = Arrays.asList(
                new Accreditation("id1", userId, AccreditationType.BY_INCOME, AccreditationStatus.CONFIRMED, LocalDateTime.now()),
                new Accreditation("id2", userId, AccreditationType.BY_NET_WORTH, AccreditationStatus.FAILED, LocalDateTime.now())
        );

        when(accreditationRepository.findAccreditationsForUser(userId)).thenReturn(accreditations);

        UserAccreditationsResponseDTO response = accreditationService.findAllAccreditationsForUser(userId);

        assertEquals(userId, response.getUserId());
        assertEquals(2, response.getAccreditationStatuses().size());

        Map<String, AccreditationStatusResponseDTO> accreditationStatuses = response.getAccreditationStatuses();
        assertEquals(AccreditationType.BY_INCOME, accreditationStatuses.get("id1").getAccreditationType());
        assertEquals(AccreditationStatus.CONFIRMED, accreditationStatuses.get("id1").getStatus());

        assertEquals(AccreditationType.BY_NET_WORTH, accreditationStatuses.get("id2").getAccreditationType());
        assertEquals(AccreditationStatus.FAILED, accreditationStatuses.get("id2").getStatus());

        verify(accreditationRepository, times(1)).findAccreditationsForUser(userId);
    }

    @Test
    void testFinalizeAccreditation_Success_PendingToConfirmed() throws APIException {
        String accreditationId = "acc123";
        Accreditation accreditation = new Accreditation(
                accreditationId,
                "user123",
                AccreditationType.BY_INCOME,
                AccreditationStatus.PENDING,
                LocalDateTime.now()
        );

        when(accreditationRepository.findAccreditation(accreditationId)).thenReturn(accreditation);

        AccreditationResponseDTO response = accreditationService.finalizeAccreditation(accreditationId, AccreditationOutcome.CONFIRMED);

        assertNotNull(response);
        assertEquals(accreditationId, response.getAccreditationId());
        assertEquals(AccreditationStatus.CONFIRMED, accreditation.getStatus());
        verify(accreditationRepository, times(1)).findAccreditation(accreditationId);
    }

    @Test
    void testFinalizeAccreditation_Success_ConfirmedToExpired() throws APIException {
        String accreditationId = "acc123";
        Accreditation accreditation = new Accreditation(
                accreditationId,
                "user123",
                AccreditationType.BY_INCOME,
                AccreditationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        when(accreditationRepository.findAccreditation(accreditationId)).thenReturn(accreditation);

        AccreditationResponseDTO response = accreditationService.finalizeAccreditation(accreditationId, AccreditationOutcome.EXPIRED);

        assertNotNull(response);
        assertEquals(accreditationId, response.getAccreditationId());
        assertEquals(AccreditationStatus.EXPIRED, accreditation.getStatus());
        verify(accreditationRepository, times(1)).findAccreditation(accreditationId);
    }

    @Test
    void testFinalizeAccreditation_Failure_AlreadyExpired() {
        String accreditationId = "acc456";
        Accreditation accreditation = new Accreditation(
                accreditationId,
                "user456",
                AccreditationType.BY_NET_WORTH,
                AccreditationStatus.EXPIRED,
                LocalDateTime.now()
        );

        when(accreditationRepository.findAccreditation(accreditationId)).thenReturn(accreditation);

        APIException exception = assertThrows(APIException.class, () -> {
            accreditationService.finalizeAccreditation(accreditationId, AccreditationOutcome.CONFIRMED);
        });

        assertEquals("Accreditation is already in EXPIRED state.", exception.getMessage());
        verify(accreditationRepository, times(1)).findAccreditation(accreditationId);
    }

    @Test
    void testFinalizeAccreditation_Failure_AlreadyFailed() {
        String accreditationId = "acc456";
        Accreditation accreditation = new Accreditation(
                accreditationId,
                "user456",
                AccreditationType.BY_NET_WORTH,
                AccreditationStatus.FAILED,
                LocalDateTime.now()
        );

        when(accreditationRepository.findAccreditation(accreditationId)).thenReturn(accreditation);

        APIException exception = assertThrows(APIException.class, () -> {
            accreditationService.finalizeAccreditation(accreditationId, AccreditationOutcome.CONFIRMED);
        });

        assertEquals("Accreditation is already in FAILED state.", exception.getMessage());
        verify(accreditationRepository, times(1)).findAccreditation(accreditationId);
    }

    @Test
    void testFinalizeAccreditation_Failure_AccreditationDoesNotExist() {
        String accreditationId = "id";
        when(accreditationRepository.findAccreditation(accreditationId)).thenReturn(null);

        APIException exception = assertThrows(APIException.class, () -> {
            accreditationService.finalizeAccreditation(accreditationId, AccreditationOutcome.CONFIRMED);
        });

        assertEquals("Accreditation does not exist.", exception.getMessage());
        verify(accreditationRepository, times(1)).findAccreditation(accreditationId);
    }
}

