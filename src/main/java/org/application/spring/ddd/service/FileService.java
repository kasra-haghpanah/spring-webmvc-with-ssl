package org.application.spring.ddd.service;

import org.application.spring.ddd.dto.FileDto;
import org.application.spring.ddd.model.entity.File;
import org.application.spring.ddd.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService extends AppService<File, String, FileRepository> implements FileRepository {

    public FileService(FileRepository fileRepository) {
        super(fileRepository);
    }


    @Override
    public List<FileDto> findByOwnerList(List<String> owners) {
        return repository.findByOwnerList(owners);
    }

    public List<FileDto> findByOwnerList(String... owners) {
        if (owners.length > 0) {
            return repository.findByOwnerList(List.of(owners));
        }
        return null;
    }

}
