// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.List;
import java.util.Map;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Event;

public record PresentationBookerWithParticipants(
    String firstName,
    String lastName,
    String email,
    String emailSignature,
    Map<Event, List<ActivityParticipant>> participants
) {}
