// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import org.montrealjug.billetterie.entity.Participant;

public record PresentationActivityParticipant(PresentationActivity presentationActivity, Participant participant) {
    public boolean isWaiting() {
        return presentationActivity
            .waitingParticipants()
            .stream()
            .anyMatch(activityParticipant ->
                activityParticipant.getActivityParticipantKey().getParticipantId() ==
                activityParticipant.getParticipant().getId()
            );
    }
}
