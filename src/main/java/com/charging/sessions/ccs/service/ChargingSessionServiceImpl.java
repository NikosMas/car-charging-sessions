package com.charging.sessions.ccs.service;

import com.charging.sessions.ccs.model.ChargingSession;
import com.charging.sessions.ccs.payload.ChargingSessionRequestPayload;
import com.charging.sessions.ccs.payload.ChargingSessionResponsePayload;
import com.charging.sessions.ccs.payload.ChargingSessionSummaryPayload;
import com.charging.sessions.ccs.repository.ChargingSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.charging.sessions.ccs.model.SessionStatus.FINISHED;
import static com.charging.sessions.ccs.model.SessionStatus.IN_PROGRESS;


@Slf4j
@Service
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private ChargingSessionRepository sessionRepository;

    public ChargingSessionServiceImpl(ChargingSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public ChargingSessionResponsePayload submitChargingSession(ChargingSessionRequestPayload request) {
        var session = sessionRepository.addNewSession(request.getStationId());

        log.info("A new charging session submitted successfully. Session id: {}", session.getId());
        return mapToPayload(session);
    }

    @Override
    public ChargingSessionResponsePayload stopChargingSession(UUID sessionId) {
        // validate that session id is valid
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Charging session with id: {} not found", sessionId);
                    return new IllegalArgumentException();
                });

        // validate that the session is IN_PROGRESS status
        if (!session.getStatus().equals(IN_PROGRESS)) {
            log.error("Charging session with {} is already stopped", sessionId);
            throw new IllegalStateException();
        }

        var stoppedSession = sessionRepository.stopSession(session);
        log.info("The charging session {} stopped successfully", session.getId());
        return mapToPayload(stoppedSession);
    }

    @Override
    public Set<ChargingSessionResponsePayload> fetchChargingSessions() {
        return sessionRepository.findAllSessions()
                .stream()
                .map(this::mapToPayload)
                .collect(Collectors.toSet());
    }

    @Override
    public ChargingSessionSummaryPayload fetchSessionsSummary() {
        var sessions = sessionRepository.findAllSessions();
        var lastMinute = Date.from(ZonedDateTime.now().minusMinutes(1).toInstant());

        var startedCount = sessions.stream()
                .filter(s -> s.getStatus().equals(IN_PROGRESS))
                .filter(s -> s.getStartedAt().after(lastMinute))
                .count();

        var finishedCount = sessions.stream()
                .filter(s -> s.getStatus().equals(FINISHED))
                .filter(s -> s.getStoppedAt() != null)
                .filter(s -> s.getStoppedAt().after(lastMinute))
                .count();

        var totalCount = startedCount + finishedCount;

        return new ChargingSessionSummaryPayload(totalCount, startedCount, finishedCount);
    }

    private ChargingSessionResponsePayload mapToPayload(ChargingSession session) {
        return new ChargingSessionResponsePayload(
                session.getId().toString(), session.getStationId(), session.getStartedAt(),
                session.getStoppedAt(), session.getStatus().name());
    }

}
