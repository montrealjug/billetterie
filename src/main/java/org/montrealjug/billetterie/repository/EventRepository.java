// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import java.util.Optional;
import org.montrealjug.billetterie.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
    // Eager fetch activities, as we will always access them
    @EntityGraph(attributePaths = { "activities" })
    Optional<Event> findByActiveIsTrue();
}
