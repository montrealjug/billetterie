// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import java.util.List;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.ActivityParticipantKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ActivityParticipantRepository extends CrudRepository<ActivityParticipant, ActivityParticipantKey> {
    @Query(
        "SELECT ap FROM ActivityParticipant ap " +
        "LEFT JOIN FETCH ap.activity a " +
        "LEFT JOIN FETCH a.event e " +
        "LEFT JOIN FETCH ap.participant p " +
        "LEFT JOIN FETCH p.booker b " +
        "WHERE e.id = :eventId AND b.email = :bookerEmail"
    )
    List<ActivityParticipant> findAllActivityParticipantByEventIdAndBookerEmail(
        @Param("eventId") long eventId,
        @Param("bookerEmail") String bookerEmail
    );
}
