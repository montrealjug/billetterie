// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import org.montrealjug.billetterie.entity.Activity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity, Long> {}
