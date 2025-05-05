// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;

public class Utils {

    static List<PresentationActivity> toIndexActivities(Set<Activity> activities) {
        List<PresentationActivity> indexActivities = new ArrayList<>();
        activities.forEach(
                activity -> {
                    // Count regular participants
                    int regularParticipants =
                            (int)
                                    activity.getParticipants().stream()
                                            .filter(p -> !p.isWaiting())
                                            .count();

                    // Count waiting participants
                    int waitingParticipants =
                            (int)
                                    activity.getParticipants().stream()
                                            .filter(ActivityParticipant::isWaiting)
                                            .count();

                    PresentationActivity indexActivity =
                            new PresentationActivity(
                                    activity.getId(),
                                    activity.getTitle(),
                                    activity.getDescription(),
                                    activity.getMaxParticipants(),
                                    activity.getMaxWaitingQueue(),
                                    regularParticipants,
                                    waitingParticipants,
                                    activity.getStartTime().toLocalTime());
                    indexActivities.add(indexActivity);
                });
        return indexActivities;
    }
}
