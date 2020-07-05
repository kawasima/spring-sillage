package net.unit8.sillage.example.user;

import net.unit8.sillage.example.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
  long countByEmail(String email);
}
