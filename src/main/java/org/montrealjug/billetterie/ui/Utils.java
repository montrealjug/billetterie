// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.montrealjug.billetterie.entity.Activity;

public class Utils {

    static List<PresentationActivity> toIndexActivities(Set<Activity> activities) {
        List<PresentationActivity> indexActivities = new ArrayList<>();
        activities.forEach(activity -> {
            PresentationActivity indexActivity = new PresentationActivity(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getMaxParticipants(),
                activity.getMaxWaitingQueue(),
                activity.getWaitingParticipants(),
                activity.getNonWaitingParticipants(),
                activity.getRegistrationStatus(),
                activity.getStartTime().toLocalTime()
            );
            indexActivities.add(indexActivity);
        });
        return indexActivities;
    }
}
