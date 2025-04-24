// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ActivityParticipantKey {

    private long activityId;
    private long participantId;

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActivityParticipantKey that)) {
            return false;
        }
        return activityId == that.activityId && participantId == that.participantId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, participantId);
    }
}
