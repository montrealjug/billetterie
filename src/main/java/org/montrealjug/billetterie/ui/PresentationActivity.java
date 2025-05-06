// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.List;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;

public record PresentationActivity(
        Long id,
        @NotBlank String title,
        @NotBlank String description,
        int maxParticipants,
        int maxWaitingQueue,
        List<ActivityParticipant> waitingParticipants,
        List<ActivityParticipant> participants,
        Activity.RegistrationStatus registrationStatus,
        LocalTime time) {}
