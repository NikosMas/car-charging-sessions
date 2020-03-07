package com.charging.sessions.ccs.controller;

import com.charging.sessions.ccs.payload.ChargingSessionRequestPayload;
import com.charging.sessions.ccs.payload.ChargingSessionResponsePayload;
import com.charging.sessions.ccs.payload.ChargingSessionSummaryPayload;
import com.charging.sessions.ccs.service.ChargingSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.charging.sessions.ccs.model.SessionStatus.FINISHED;
import static com.charging.sessions.ccs.model.SessionStatus.IN_PROGRESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ChargingSessionControllerTest {

    @MockBean
    private ChargingSessionService chargingSessionService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddSession() throws Exception {
        ChargingSessionResponsePayload responsePayload = getNewChargingSessionPayload("stationA");

        when(chargingSessionService.submitChargingSession(any())).thenReturn(responsePayload);

        String body = objectMapper.writeValueAsString(new ChargingSessionRequestPayload("stationA"));
        this.mockMvc.perform(post("/chargingSessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responsePayload.getId()))
                .andExpect(jsonPath("$.stationId").value(responsePayload.getStationId()));
    }

    @Test
    public void testAddSessionWithBlankStationId() throws Exception {
        String body = objectMapper.writeValueAsString(new ChargingSessionRequestPayload(""));
        this.mockMvc.perform(post("/chargingSessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testStopSessionSuccessfully() throws Exception {
        ChargingSessionResponsePayload responsePayload = getStoppedChargingSessionPayload("stationA");

        when(chargingSessionService.stopChargingSession(any())).thenReturn(responsePayload);

        this.mockMvc.perform(put("/chargingSessions/" + responsePayload.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responsePayload.getId()))
                .andExpect(jsonPath("$.status").value(FINISHED.name()))
                .andExpect(jsonPath("$.stoppedAt").isNotEmpty())
                .andExpect(jsonPath("$.stationId").value(responsePayload.getStationId()));
    }

    @Test
    public void testStopSessionWithInvalidUUID() throws Exception {
        this.mockMvc.perform(put("/chargingSessions/invalidId"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testStopSessionOnAlreadyStoppedSession() throws Exception {
        when(chargingSessionService.stopChargingSession(any())).thenThrow(IllegalStateException.class);

        this.mockMvc.perform(put("/chargingSessions/" + UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testStopSessionOnNotFoundSession() throws Exception {
        when(chargingSessionService.stopChargingSession(any())).thenThrow(IllegalArgumentException.class);

        this.mockMvc.perform(put("/chargingSessions/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFetchSessions() throws Exception {
        Set<ChargingSessionResponsePayload> sessions = new HashSet<>();
        ChargingSessionResponsePayload responsePayload = getNewChargingSessionPayload("stationA");
        sessions.add(responsePayload);

        when(chargingSessionService.fetchChargingSessions()).thenReturn(sessions);

        this.mockMvc.perform(get("/chargingSessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(responsePayload.getId()));
    }

    @Test
    public void testFetchSessionSummary() throws Exception {
        ChargingSessionSummaryPayload responsePayload = new ChargingSessionSummaryPayload(2, 1, 1);

        when(chargingSessionService.fetchSessionsSummary()).thenReturn(responsePayload);

        this.mockMvc.perform(get("/chargingSessions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(responsePayload.getTotalCount()))
                .andExpect(jsonPath("$.startedCount").value(responsePayload.getStartedCount()))
                .andExpect(jsonPath("$.stoppedCount").value(responsePayload.getStoppedCount()));
    }

    private ChargingSessionResponsePayload getNewChargingSessionPayload(String stationId) {
        ChargingSessionResponsePayload session = new ChargingSessionResponsePayload();
        session.setId(UUID.randomUUID().toString());
        session.setStatus(IN_PROGRESS.name());
        session.setStationId(stationId);
        session.setStartedAt(new Date());
        return session;
    }

    private ChargingSessionResponsePayload getStoppedChargingSessionPayload(String stationId) {
        ChargingSessionResponsePayload session = new ChargingSessionResponsePayload();
        session.setId(UUID.randomUUID().toString());
        session.setStatus(FINISHED.name());
        session.setStationId(stationId);
        session.setStartedAt(Date.from(ZonedDateTime.now().minusMinutes(2).toInstant()));
        session.setStoppedAt(new Date());
        return session;
    }

}