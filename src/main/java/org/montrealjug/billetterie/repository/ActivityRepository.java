// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import java.util.List;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
    @Query(
        "SELECT ap FROM ActivityParticipant ap " +
        "JOIN ap.activity a " +
        "JOIN a.event e " +
        "JOIN ap.participant p " +
        "JOIN p.booker b " +
        "WHERE e.id = :eventId AND b.email = :bookerEmail"
    )
    List<ActivityParticipant> findAllActivityParticipantByEventIdAndBookerEmail(
        @Param("eventId") long eventId,
        @Param("bookerEmail") String bookerEmail
    );
}
