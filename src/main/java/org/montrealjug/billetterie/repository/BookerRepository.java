// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import java.util.Optional;
import org.montrealjug.billetterie.entity.Booker;
import org.springframework.data.repository.CrudRepository;

public interface BookerRepository extends CrudRepository<Booker, String> {
    Optional<Booker> findByEmailSignature(String emailSignature);
}
