package org.application.spring.ddd.repository;

import org.application.spring.ddd.dto.FileDto;
import org.application.spring.ddd.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {

    @Query("SELECT new org.application.spring.ddd.dto.FileDto(f.id, f.type, f.name, f.ownerId) FROM File f WHERE f.ownerId IN (:owners)")
    List<FileDto> findByOwnerList(@Param("owners") List<String> owners);

}
