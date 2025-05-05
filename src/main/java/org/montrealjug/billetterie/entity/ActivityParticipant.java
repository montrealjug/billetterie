// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.time.Instant;
import java.util.Objects;

@Entity
public class ActivityParticipant implements Comparable<ActivityParticipant> {

    @EmbeddedId
    private ActivityParticipantKey activityParticipantKey = new ActivityParticipantKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")
    private Activity activity;

    @ManyToOne
    @MapsId("participantId")
    private Participant participant;

    @Column(nullable = false, updatable = false)
    private Instant registrationTime = Instant.now();

    @Column(nullable = false)
    private boolean isWaiting = false;

    public ActivityParticipantKey getActivityParticipantKey() {
        return activityParticipantKey;
    }

    public void setActivityParticipantKey(ActivityParticipantKey activityParticipantKey) {
        this.activityParticipantKey = activityParticipantKey;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Instant registrationTime) {
        this.registrationTime = registrationTime;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActivityParticipant that)) {
            return false;
        }
        return Objects.equals(activityParticipantKey, that.activityParticipantKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(activityParticipantKey);
    }

    @Override
    public int compareTo(ActivityParticipant o) {
        var activityComp =
                Long.compare(
                        this.activityParticipantKey.getActivityId(),
                        o.activityParticipantKey.getActivityId());
        if (activityComp == 0) {
            return this.registrationTime.compareTo(o.registrationTime);
        } else {
            return activityComp;
        }
    }
}
