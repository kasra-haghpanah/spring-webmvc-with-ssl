package org.application.spring.ddd.repository;

import org.application.spring.ddd.dto.FileDto;
import org.application.spring.ddd.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Modifying
    @Query(value = "DELETE c, f FROM customer c JOIN file f ON c.id = f.owner_id WHERE c.id IN (:ids)", nativeQuery = true)
    int deleteById(@Param("ids") List<String> ids);

}
