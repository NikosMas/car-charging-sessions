package com.charging.sessions.ccs.controller;

import com.charging.sessions.ccs.payload.ChargingSessionRequestPayload;
import com.charging.sessions.ccs.payload.ChargingSessionResponsePayload;
import com.charging.sessions.ccs.payload.ChargingSessionSummaryPayload;
import com.charging.sessions.ccs.service.ChargingSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("chargingSessions")
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;

    public ChargingSessionController(ChargingSessionService chargingSessionService) {
        this.chargingSessionService = chargingSessionService;
    }

    @PostMapping
    public ChargingSessionResponsePayload addSession(@Valid @RequestBody ChargingSessionRequestPayload request) {
        log.info("Request to add a new charging session. Station: {}", request.getStationId());
        return chargingSessionService.submitChargingSession(request);
    }

    @PutMapping("{id}")
    public ResponseEntity<ChargingSessionResponsePayload> stopSession(@PathVariable String id) {
        ChargingSessionResponsePayload chargingSession;
        try {
            UUID sessionId = UUID.fromString(id);
            log.info("Request to stop a charging session. Id: {}", sessionId);
            chargingSession = chargingSessionService.stopChargingSession(sessionId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(chargingSession);
    }

    @GetMapping
    public Set<ChargingSessionResponsePayload> fetchSessions() {
        log.info("Request to retrieve all the charging sessions");
        return chargingSessionService.fetchChargingSessions();
    }

    @GetMapping("summary")
    public ChargingSessionSummaryPayload fetchSessionSummary() {
        log.info("Request to retrieve a latest summary of submitted charging sessions");
        return chargingSessionService.fetchSessionsSummary();
    }

}
