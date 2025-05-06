// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public record PresentationActivity(
        Long id,
        @NotBlank String title,
        @NotBlank String description,
        int maxParticipants,
        int maxWaitingQueue,
        int currentParticipants,
        int currentWaitingParticipants,
        LocalTime time) {}
