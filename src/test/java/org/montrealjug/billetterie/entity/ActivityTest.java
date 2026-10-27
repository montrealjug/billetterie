// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.TreeSet;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ActivityTest {

    @ParameterizedTest
    @CsvSource({
        "0, OPEN",
        "5, OPEN",
        // all the seats are taken: the next one goes to the waiting list
        "6, WAITING_LIST",
        "7, WAITING_LIST",
        // seats and waiting list are full
        "8, CLOSED",
        "9, CLOSED",
    })
    void registrationStatus_should_tell_where_the_next_registration_goes(
        int nbParticipants,
        Activity.RegistrationStatus expected
    ) {
        var activity = activity(6, 2, nbParticipants);

        assertThat(activity.getRegistrationStatus()).isEqualTo(expected);
    }

    @Test
    void waitingParticipants_should_include_participants_registered_beyond_the_waiting_queue() {
        var activity = activity(6, 2, 9);

        assertThat(activity.getNonWaitingParticipants()).hasSize(6);
        assertThat(activity.getWaitingParticipants()).hasSize(3);
    }

    private static Activity activity(int maxParticipants, int maxWaitingQueue, int nbParticipants) {
        var activity = new Activity();
        activity.setId(1L);
        activity.setMaxParticipants(maxParticipants);
        activity.setMaxWaitingQueue(maxWaitingQueue);
        var participants = new TreeSet<ActivityParticipant>();
        var start = Instant.parse("2026-10-24T14:46:15Z");
        LongStream.range(0, nbParticipants).forEach(i -> {
            var activityParticipant = new ActivityParticipant();
            activityParticipant.getActivityParticipantKey().setActivityId(1L);
            activityParticipant.getActivityParticipantKey().setParticipantId(i);
            activityParticipant.setRegistrationTime(start.plusSeconds(i));
            participants.add(activityParticipant);
        });
        activity.setParticipants(participants);
        return activity;
    }
}
