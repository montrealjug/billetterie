// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Participant;

public class Utils {

    static List<PresentationActivity> toPresentationActivities(Set<Activity> activities) {
        return activities.stream().map((Activity activity) -> toPresentationActivity(activity, true)).toList();
    }

    static List<PresentationActivity> toPresentationActivitiesLimitedToBooker(
        Set<Activity> activities,
        Set<Participant> bookerParticipants
    ) {
        return activities
            .stream()
            .map((Activity activity) -> toPresentationActivity(activity, bookerParticipants, true))
            .toList();
    }

    static List<PresentationActivityParticipant> toPresentationActivityParticipants(List<ActivityParticipant> aps) {
        return aps.stream().map(Utils::toPresentationParticipantActivity).toList();
    }

    static PresentationActivityParticipant toPresentationParticipantActivity(ActivityParticipant ap) {
        return new PresentationActivityParticipant(
            toPresentationActivity(ap.getActivity(), false),
            ap.getParticipant()
        );
    }

    static PresentationActivity toPresentationActivity(Activity activity, boolean html) {
        return new PresentationActivity(
            activity.getId(),
            activity.getTitle(),
            html ? markdownToHtml(activity.getDescription()) : activity.getDescription(),
            activity.getMaxParticipants(),
            activity.getMaxWaitingQueue(),
            activity.getParticipants().size(),
            activity.getWaitingParticipants().size(),
            activity.getWaitingParticipants(),
            activity.getNonWaitingParticipants(),
            activity.getRegistrationStatus(),
            activity.getStartTime().toLocalTime(),
            activity.getImagePath()
        );
    }

    static PresentationActivity toPresentationActivity(
        Activity activity,
        Set<Participant> bookerParticipants,
        boolean html
    ) {
        return new PresentationActivity(
            activity.getId(),
            activity.getTitle(),
            html ? markdownToHtml(activity.getDescription()) : activity.getDescription(),
            activity.getMaxParticipants(),
            activity.getMaxWaitingQueue(),
            activity.getParticipants().size(),
            activity.getWaitingParticipants().size(),
            activity
                .getWaitingParticipants()
                .stream()
                .filter(ap -> bookerParticipants.contains(ap.getParticipant()))
                .toList(),
            activity
                .getNonWaitingParticipants()
                .stream()
                .filter(ap -> bookerParticipants.contains(ap.getParticipant()))
                .toList(),
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
