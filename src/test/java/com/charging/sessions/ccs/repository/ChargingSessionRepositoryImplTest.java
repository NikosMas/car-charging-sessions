package com.charging.sessions.ccs.repository;

import com.charging.sessions.ccs.model.ChargingSession;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.charging.sessions.ccs.model.SessionStatus.FINISHED;
import static com.charging.sessions.ccs.model.SessionStatus.IN_PROGRESS;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ChargingSessionRepositoryImplTest {

    @InjectMocks
    private ChargingSessionRepositoryImpl chargingSessionRepository;

    @After
    public void clear() {
        chargingSessionRepository.deleteAllSessions();
    }

    @Test
    public void testAddNewSession() {
        ChargingSession newSession = chargingSessionRepository.addNewSession("stationA");
        Set<ChargingSession> sessions = chargingSessionRepository.findAllSessions();

        assertEquals("stationA", newSession.getStationId());
        assertEquals(IN_PROGRESS, newSession.getStatus());
        assertTrue(sessions.contains(newSession));
    }

    @Test
    public void testStopSession() {
        ChargingSession newSession = chargingSessionRepository.addNewSession("stationA");
        ChargingSession stoppedSession = chargingSessionRepository.stopSession(newSession);

        assertEquals(newSession.getId(), stoppedSession.getId());
        assertEquals(stoppedSession.getStatus(), FINISHED);
        assertNotNull(stoppedSession.getStoppedAt());
    }

    @Test
    public void testFindAllSessions() {
        ChargingSession newSessionA = chargingSessionRepository.addNewSession("stationA");
        ChargingSession newSessionB = chargingSessionRepository.addNewSession("stationB");

        Set<ChargingSession> allSessions = chargingSessionRepository.findAllSessions();

        assertTrue(allSessions.contains(newSessionA));
        assertTrue(allSessions.contains(newSessionB));
    }

    @Test
    public void testFindAllSessionsWithEmptyResult() {
        Set<ChargingSession> allSessions = chargingSessionRepository.findAllSessions();
        assertTrue(allSessions.isEmpty());
    }

    @Test
    public void testFindById() {
        ChargingSession newSession = chargingSessionRepository.addNewSession("stationA");
        Optional<ChargingSession> session = chargingSessionRepository.findById(newSession.getId());

        assertNotEquals(Optional.empty(), session);
        assertNotNull(session.get());
        assertEquals(session.get().getStationId(), newSession.getStationId());
    }

    @Test
    public void testFindByIdWithRandomId() {
        chargingSessionRepository.addNewSession("stationA");
        Optional<ChargingSession> session = chargingSessionRepository.findById(UUID.randomUUID());

        assertEquals(Optional.empty(), session);
    }

}