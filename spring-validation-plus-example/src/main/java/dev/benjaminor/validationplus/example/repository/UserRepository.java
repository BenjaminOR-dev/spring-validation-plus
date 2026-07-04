package dev.benjaminor.validationplus.example.repository;

import dev.benjaminor.validationplus.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
