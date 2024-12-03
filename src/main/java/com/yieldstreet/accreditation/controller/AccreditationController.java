package com.yieldstreet.accreditation.controller;

import com.yieldstreet.accreditation.exception.APIException;
import com.yieldstreet.accreditation.model.AccreditationFinalizationRequest;
import com.yieldstreet.accreditation.model.AccreditationRequest;
import com.yieldstreet.accreditation.model.AccreditationResponse;
import com.yieldstreet.accreditation.service.AccreditationService;
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
    public ResponseEntity<AccreditationResponse> createAccreditationStatus(
            @RequestBody AccreditationRequest request) {
        AccreditationResponse response = accreditationService.processAccreditation(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accreditation/{accreditationId}")
    public ResponseEntity<AccreditationResponse> finalizeAccreditation(
            @PathVariable String accreditationId,
            @RequestBody AccreditationFinalizationRequest request) throws APIException {
        AccreditationResponse response = accreditationService.finalizeAccreditation(accreditationId, request.getOutcome());
        return ResponseEntity.ok(response);
    }
}
