package com.charging.sessions.ccs.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingSessionRequestPayload {

    @NotBlank
    private String stationId;
}
