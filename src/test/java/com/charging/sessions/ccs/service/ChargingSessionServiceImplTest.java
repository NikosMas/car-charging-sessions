package com.charging.sessions.ccs.service;

import com.charging.sessions.ccs.model.ChargingSession;
import com.charging.sessions.ccs.payload.ChargingSessionRequestPayload;
import com.charging.sessions.ccs.payload.ChargingSessionResponsePayload;
import com.charging.sessions.ccs.payload.ChargingSessionSummaryPayload;
import com.charging.sessions.ccs.repository.ChargingSessionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.*;

import static com.charging.sessions.ccs.model.SessionStatus.FINISHED;
import static com.charging.sessions.ccs.model.SessionStatus.IN_PROGRESS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ChargingSessionServiceImplTest {

    @Mock
    private ChargingSessionRepository chargingSessionRepository;

    @InjectMocks
    private ChargingSessionServiceImpl chargingSessionService;

    @Test
    public void testSubmitChargingSession() {
        ChargingSession session = getNewChargingSession("stationA");

        when(chargingSessionRepository.addNewSession(anyString())).thenReturn(session);

        ChargingSessionRequestPayload request = new ChargingSessionRequestPayload("stationA");
        ChargingSessionResponsePayload payload = chargingSessionService.submitChargingSession(request);

        assertEquals(IN_PROGRESS.name(), payload.getStatus());
        assertEquals(request.getStationId(), payload.getStationId());
    }

    @Test
    public void testStopChargingSessionSuccessfully() {
        ChargingSession newSession = getNewChargingSession("stationA");
        ChargingSession stoppedSession = stopChargingSession(newSession);

        when(chargingSessionRepository.findById(any())).thenReturn(Optional.of(newSession));
        when(chargingSessionRepository.stopSession(any())).thenReturn(stoppedSession);

        ChargingSessionResponsePayload sessionPayload = chargingSessionService.stopChargingSession(newSession.getId());
        assertEquals(newSession.getId().toString(), sessionPayload.getId());
        assertEquals(FINISHED.name(), sessionPayload.getStatus());
        assertNotNull(sessionPayload.getStoppedAt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopChargingSessionWithRandomId() {
        when(chargingSessionRepository.findById(any())).thenReturn(Optional.empty());
        chargingSessionService.stopChargingSession(UUID.randomUUID());
    }

    @Test(expected = IllegalStateException.class)
    public void testStopChargingSessionWhenAlreadyStopped() {
        ChargingSession newSession = getNewChargingSession("stationA");
        ChargingSession stoppedSession = stopChargingSession(newSession);

        when(chargingSessionRepository.findById(any())).thenReturn(Optional.of(stoppedSession));

        chargingSessionService.stopChargingSession(newSession.getId());
    }

    @Test
    public void testFetchChargingSessions() {
        Set<ChargingSession> sessions = new HashSet<>();
        sessions.add(getNewChargingSession("stationA"));
        sessions.add(getNewChargingSession("stationB"));

        when(chargingSessionRepository.findAllSessions()).thenReturn(sessions);

        Set<ChargingSessionResponsePayload> allSessions = chargingSessionService.fetchChargingSessions();
        assertTrue(allSessions.stream().anyMatch(s -> s.getStationId().equals("stationA")));
        assertTrue(allSessions.stream().anyMatch(s -> s.getStationId().equals("stationB")));
    }

    @Test
    public void testFetchChargingSessionsWithEmptyResult() {
        when(chargingSessionRepository.findAllSessions()).thenReturn(new HashSet<>());

        Set<ChargingSessionResponsePayload> allSessions = chargingSessionService.fetchChargingSessions();
        assertTrue(allSessions.isEmpty());
    }

    @Test
    public void testFetchSessionsSummaryCaseA() {
        Set<ChargingSession> sessions = new HashSet<>();
        sessions.add(getNewChargingSession("stationA"));
        sessions.add(getNewChargingSession("stationB"));

        ChargingSession sessionC = getNewChargingSession("stationC");
        ChargingSession stoppedSessionC = stopChargingSession(sessionC);
        sessions.add(stoppedSessionC);

        when(chargingSessionRepository.findAllSessions()).thenReturn(sessions);

        ChargingSessionSummaryPayload summary = chargingSessionService.fetchSessionsSummary();

        assertEquals(1, summary.getStoppedCount());
        assertEquals(2, summary.getStartedCount());
        assertEquals(3, summary.getTotalCount());
    }

    @Test
    public void testFetchSessionsSummaryCaseB() {
        Set<ChargingSession> sessions = new HashSet<>();
        sessions.add(getNewChargingSession("stationA"));
        ChargingSession sessionB = getNewChargingSession("stationB");
        sessionB.setStartedAt(Date.from(ZonedDateTime.now().minusMinutes(2).toInstant()));
        sessions.add(sessionB);

        ChargingSession sessionC = getNewChargingSession("stationC");
        ChargingSession stoppedSession = stopChargingSession(sessionC);
        sessions.add(stoppedSession);

        when(chargingSessionRepository.findAllSessions()).thenReturn(sessions);

        ChargingSessionSummaryPayload summary = chargingSessionService.fetchSessionsSummary();

        assertEquals(1, summary.getStoppedCount());
        assertEquals(1, summary.getStartedCount());
        assertEquals(2, summary.getTotalCount());
    }

    @Test
    public void testFetchSessionsSummaryCaseC() {
        Set<ChargingSession> sessions = new HashSet<>();
        sessions.add(getNewChargingSession("stationA"));
        ChargingSession sessionB = getNewChargingSession("stationB");
        sessionB.setStartedAt(Date.from(ZonedDateTime.now().minusMinutes(2).toInstant()));

        ChargingSession stoppedSession = stopChargingSession(sessionB);
        sessions.add(stoppedSession);

        when(chargingSessionRepository.findAllSessions()).thenReturn(sessions);

        ChargingSessionSummaryPayload summary = chargingSessionService.fetchSessionsSummary();

        assertEquals(1, summary.getStoppedCount());
        assertEquals(1, summary.getStartedCount());
        assertEquals(2, summary.getTotalCount());
    }

    private ChargingSession getNewChargingSession(String stationId) {
        ChargingSession session = new ChargingSession();
        session.setId(UUID.randomUUID());
        session.setStatus(IN_PROGRESS);
        session.setStationId(stationId);
        session.setStartedAt(new Date());
        return session;
    }

    private ChargingSession stopChargingSession(ChargingSession session) {
        ChargingSession stoppedSession = new ChargingSession();
        stoppedSession.setId(session.getId());
        stoppedSession.setStatus(FINISHED);
        stoppedSession.setStationId(session.getStationId());
        stoppedSession.setStartedAt(session.getStartedAt());
        stoppedSession.setStoppedAt(new Date());
        return stoppedSession;
    }
}