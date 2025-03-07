package org.sumire.studyhardprogram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sumire.studyhardprogram.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
