// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import java.util.List;
import org.montrealjug.billetterie.entity.ActivityParticipant;

public record PresentationBookerWithParticipants(
    String firstName,
    String lastName,
    String email,
    String emailSignature,
    List<ActivityParticipant> participants
) {}
