package net.unit8.sillage.example.persistence;

import net.unit8.sillage.example.persistence.entity.PromoteEntity;
import org.springframework.data.repository.CrudRepository;

public interface PromoteRepository extends CrudRepository<PromoteEntity, Long> {
}
