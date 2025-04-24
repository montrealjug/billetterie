package org.montrealjug.billetterie.repository;

import org.montrealjug.billetterie.entity.Booker;
import org.springframework.data.repository.CrudRepository;

public interface BookerRepository extends CrudRepository<Booker, String> {
}
