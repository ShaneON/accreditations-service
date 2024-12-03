package com.yieldstreet.accreditation.controller;

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
}
