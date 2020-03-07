package com.charging.sessions.ccs.repository;

import com.charging.sessions.ccs.model.ChargingSession;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ChargingSessionRepository {

    /**
     * Save a new charging session entity
     *
     * @param stationId the id of the station
     */
    ChargingSession addNewSession(String stationId);

    /**
     * Update an existing started charging session to stopped state
     *
     * @param session the started charging session to be stopped
     */
    ChargingSession stopSession(ChargingSession session);

    /**
     * Find all the existing charging sessions
     */
    Set<ChargingSession> findAllSessions();

    /**
     * Find a charging session by session id
     *
     * @param sessionId the charging session id to search for
     */
    Optional<ChargingSession> findById(UUID sessionId);

    /**
     * Delete all charging sessions
     */
    void deleteAllSessions();
}
