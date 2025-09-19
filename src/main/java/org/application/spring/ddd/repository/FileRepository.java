package org.application.spring.ddd.repository;

import org.application.spring.ddd.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {

    @Query("SELECT f FROM File f WHERE f.ownerId IN (:owners)")
    List<File> findByOwnerList(@Param("owners") List<String> owners);

}
