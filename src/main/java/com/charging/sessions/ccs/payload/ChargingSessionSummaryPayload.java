package com.charging.sessions.ccs.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingSessionSummaryPayload {

    private long totalCount;

    private long startedCount;

    private long stoppedCount;
}
