package com.ticket.ticketmanagement.scheduler;

import com.ticket.ticketmanagement.service.SlaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlaScheduler {

    private final SlaService slaService;

    public SlaScheduler(SlaService slaService) {
        this.slaService = slaService;
    }

    @Scheduled(fixedRate = 300000)
    public void checkSlaBreach() {
        slaService.checkSlaBreaches();
    }
}