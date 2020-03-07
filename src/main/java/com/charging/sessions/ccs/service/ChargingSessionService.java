package com.charging.sessions.ccs.service;

import com.charging.sessions.ccs.payload.ChargingSessionRequestPayload;
import com.charging.sessions.ccs.payload.ChargingSessionResponsePayload;
import com.charging.sessions.ccs.payload.ChargingSessionSummaryPayload;

import java.util.Set;
import java.util.UUID;

public interface ChargingSessionService {

    /**
     * Submit a new charging session
     *
     * @param request a {@link ChargingSessionRequestPayload} request to submit a new charging session
     */
    ChargingSessionResponsePayload submitChargingSession(ChargingSessionRequestPayload request);

    /**
     * Stop an existing started charging session
     *
     * @param sessionId the charging session id
     */
    ChargingSessionResponsePayload stopChargingSession(UUID sessionId);

    /**
     * Fetch all the charging sessions
     */
    Set<ChargingSessionResponsePayload> fetchChargingSessions();

    /**
     * Fetch a summary of charging sessions that are created or updated the last minute
     */
    ChargingSessionSummaryPayload fetchSessionsSummary();
}
