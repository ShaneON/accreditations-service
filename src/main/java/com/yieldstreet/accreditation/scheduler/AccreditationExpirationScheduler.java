package com.yieldstreet.accreditation.scheduler;

import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import com.yieldstreet.accreditation.repository.AccreditationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccreditationExpirationScheduler {

    @Autowired
    AccreditationRepository accreditationRepository;

    @Scheduled(cron = "*/10 * * * * *")
    public void setExpiredConfirmations() {

        for (Accreditation accreditation : accreditationRepository.findAllAccreditations()) {
            if (accreditation.getStatus() == AccreditationStatus.CONFIRMED &&
                    accreditation.getLastUpdateTime().isBefore(LocalDateTime.now().minusDays(30))) {
                accreditation.setStatus(AccreditationStatus.EXPIRED);
                accreditation.setLastUpdateTime(LocalDateTime.now());
            }
        }
    }
}
