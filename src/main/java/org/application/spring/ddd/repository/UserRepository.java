package org.application.spring.ddd.repository;

import org.application.spring.ddd.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
