package com.charging.sessions.ccs.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ChargingSessionResponsePayload {

    private String id;

    private String stationId;

    private Date startedAt;

    private Date stoppedAt;

    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingSessionResponsePayload that = (ChargingSessionResponsePayload) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
