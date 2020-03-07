package com.charging.sessions.ccs.repository;

import com.charging.sessions.ccs.model.ChargingSession;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.charging.sessions.ccs.model.SessionStatus.FINISHED;
import static com.charging.sessions.ccs.model.SessionStatus.IN_PROGRESS;

@Repository
public class ChargingSessionRepositoryImpl implements ChargingSessionRepository {

    /**
     * The {@link HashSet} to save the charging sessions
     */
    private Set<ChargingSession> chargingSessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public ChargingSession addNewSession(String stationId) {
        var session = new ChargingSession();
        session.setId(UUID.randomUUID());
        session.setStatus(IN_PROGRESS);
        session.setStationId(stationId);
        session.setStartedAt(new Date());

        // add a new charging session in the Set of sessions
        chargingSessions.add(session);
        return session;
    }

    @Override
    public ChargingSession stopSession(ChargingSession session) {
        session.setStoppedAt(new Date());
        session.setStatus(FINISHED);
        return session;
    }

    @Override
    public Set<ChargingSession> findAllSessions() {
        return chargingSessions;
    }

    @Override
    public Optional<ChargingSession> findById(UUID sessionId) {
        return chargingSessions
                .stream()
                .filter(cs -> cs.getId().equals(sessionId))
                .findFirst();
    }

    @Override
    public void deleteAllSessions() {
        chargingSessions.clear();
    }

}
