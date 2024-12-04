package com.yieldstreet.accreditation.controller;

import com.yieldstreet.accreditation.dto.AccreditationRequestDTO;
import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.dto.AccreditationFinalizationRequestDTO;
import com.yieldstreet.accreditation.dto.AccreditationResponseDTO;
import com.yieldstreet.accreditation.service.AccreditationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AccreditationController {

    private final AccreditationService accreditationService;

    public AccreditationController(AccreditationService accreditationService) {
        this.accreditationService = accreditationService;
    }

    @PostMapping("/accreditation")
    public ResponseEntity<AccreditationResponseDTO> createAccreditationStatus(
            @Valid @RequestBody AccreditationRequestDTO request) {
        AccreditationResponseDTO response = accreditationService.processAccreditation(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accreditation/{accreditationId}")
    public ResponseEntity<AccreditationResponseDTO> finalizeAccreditation(
            @PathVariable String accreditationId,
            @RequestBody AccreditationFinalizationRequestDTO request) throws APIException {
        AccreditationResponseDTO response = accreditationService.finalizeAccreditation(accreditationId, request.getOutcome());
        return ResponseEntity.ok(response);
    }
}
