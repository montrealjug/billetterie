// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import org.montrealjug.billetterie.entity.Participant;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {}
