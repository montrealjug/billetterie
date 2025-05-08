// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.List;
import java.util.Set;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.montrealjug.billetterie.entity.Activity;

public class Utils {

    static List<PresentationActivity> toPresentationActivities(Set<Activity> activities) {
        return activities.stream().map((Activity activity) -> toPresentationActivity(activity, true)).toList();
    }

    static PresentationActivity toPresentationActivity(Activity activity, boolean html) {
        return new PresentationActivity(
            activity.getId(),
            activity.getTitle(),
            html ? markdownToHtml(activity.getDescription()) : activity.getDescription(),
            activity.getMaxParticipants(),
            activity.getMaxWaitingQueue(),
            activity.getWaitingParticipants(),
            activity.getNonWaitingParticipants(),
            activity.getRegistrationStatus(),
            activity.getStartTime().toLocalTime(),
            activity.getImagePath()
        );
    }

    static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().softbreak("<br />").build();
        return renderer.render(document);
    }
}
