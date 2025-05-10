// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.time.format.DateTimeFormatter;
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

    private static final Parser MD_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().softbreak("<br />").build();

    static String markdownToHtml(String markdown) {
        Node document = MD_PARSER.parse(markdown);
        return HTML_RENDERER.render(document);
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
}
