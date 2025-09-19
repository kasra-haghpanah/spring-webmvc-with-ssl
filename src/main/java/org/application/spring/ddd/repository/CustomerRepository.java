package org.application.spring.ddd.repository;

import org.application.spring.ddd.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
