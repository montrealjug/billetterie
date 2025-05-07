// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.ActivityParticipantKey;

public class UtilsTest {

    @Test
    void toPresentationActivities_should_count_participants_and_waiting_participants() {
        // Arrange
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setTitle("Test Activity");
        activity.setDescription("Test Description");
        activity.setMaxParticipants(10);
        activity.setMaxWaitingQueue(5);
        activity.setStartTime(LocalDateTime.now());

        Set<ActivityParticipant> participants = new HashSet<>();

        // Add 10 regular participants
        for (int i = 0; i < 10; i++) {
            ActivityParticipant ap = new ActivityParticipant();
            ap.setActivity(activity);

            // Set a unique participant ID for each participant
            ActivityParticipantKey key = new ActivityParticipantKey();
            key.setActivityId(activity.getId());
            key.setParticipantId(i + 1); // Unique ID for each participant
            ap.setActivityParticipantKey(key);

            participants.add(ap);
        }

        // Add 2 waiting participants
        for (int i = 0; i < 2; i++) {
            ActivityParticipant ap = new ActivityParticipant();
            ap.setActivity(activity);

            // Set a unique participant ID for each participant
            ActivityParticipantKey key = new ActivityParticipantKey();
            key.setActivityId(activity.getId());
            key.setParticipantId(i + 100); // Unique ID for each waiting participant
            ap.setActivityParticipantKey(key);

            participants.add(ap);
        }

        activity.setParticipants(participants);

        Set<Activity> activities = new HashSet<>();
        activities.add(activity);

        // Act
        var result = Utils.toPresentationActivities(activities);

        // Assert
        assertThat(result).hasSize(1);
        var presentationActivity = result.getFirst();
        assertThat(presentationActivity.id()).isEqualTo(1L);
        assertThat(presentationActivity.title()).isEqualTo("Test Activity");
        assertThat(presentationActivity.description()).isEqualTo("Test Description");
        assertThat(presentationActivity.maxParticipants()).isEqualTo(10);
        assertThat(presentationActivity.maxWaitingQueue()).isEqualTo(5);
        assertThat(presentationActivity.participants().size()).isEqualTo(10);
        assertThat(presentationActivity.waitingParticipants().size()).isEqualTo(2);
        assertThat(presentationActivity.registrationStatus()).isEqualTo(Activity.RegistrationStatus.WAITING_LIST);
    }
}
