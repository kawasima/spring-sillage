package net.unit8.sillage.example.persistence;

import net.unit8.sillage.example.persistence.entity.EmployeeEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<EmployeeEntity, Long> {
    long countByEmail(String email);
}
