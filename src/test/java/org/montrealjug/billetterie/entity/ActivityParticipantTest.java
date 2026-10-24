// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class ActivityParticipantTest {

    @Test
    void sorted_set_should_not_hold_two_entries_for_the_same_activity_and_participant() {
        var first = activityParticipant(1L, 10L, Instant.parse("2026-10-24T14:46:15Z"));
        // same key, registered later (e.g. a duplicate submission)
        var duplicate = activityParticipant(1L, 10L, Instant.parse("2026-10-24T14:46:18Z"));

        var participants = new TreeSet<ActivityParticipant>();
        participants.add(first);

        assertThat(participants.add(duplicate)).isFalse();
        assertThat(participants).containsExactly(first);
    }

    @Test
    void sorted_set_should_keep_distinct_participants_registered_at_the_same_time() {
        var sameTime = Instant.parse("2026-10-24T14:46:15Z");
        var later = activityParticipant(1L, 10L, sameTime.plusSeconds(1));
        var second = activityParticipant(1L, 12L, sameTime);
        var first = activityParticipant(1L, 11L, sameTime);

        var participants = new TreeSet<ActivityParticipant>();
        participants.add(later);
        participants.add(second);
        participants.add(first);

        assertThat(participants).containsExactly(first, second, later);
    }

    private static ActivityParticipant activityParticipant(
        long activityId,
        long participantId,
        Instant registrationTime
    ) {
        var activityParticipant = new ActivityParticipant();
        activityParticipant.getActivityParticipantKey().setActivityId(activityId);
        activityParticipant.getActivityParticipantKey().setParticipantId(participantId);
        activityParticipant.setRegistrationTime(registrationTime);
        return activityParticipant;
    }
}
