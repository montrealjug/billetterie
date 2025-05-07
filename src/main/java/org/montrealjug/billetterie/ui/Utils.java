// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.List;
import java.util.Set;
import org.montrealjug.billetterie.entity.Activity;

public class Utils {

    static List<PresentationActivity> toPresentationActivities(Set<Activity> activities) {
        return activities.stream().map(Utils::toPresentationActivity).toList();
    }

    static PresentationActivity toPresentationActivity(Activity activity) {
        return new PresentationActivity(
            activity.getId(),
            activity.getTitle(),
            activity.getDescription(),
            activity.getMaxParticipants(),
            activity.getMaxWaitingQueue(),
            activity.getWaitingParticipants(),
            activity.getNonWaitingParticipants(),
            activity.getRegistrationStatus(),
            activity.getStartTime().toLocalTime(),
            activity.getImagePath()
        );
    }
}
