package org.example.is_lab1.services;

import org.example.is_lab1.models.entity.ImportFile;
import org.example.is_lab1.models.entity.ImportStatus;
import org.example.is_lab1.repository.ImportFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportStatusService {
    private static final Logger log = LoggerFactory.getLogger(ImportStatusService.class);
    ImportFileRepository importFileRepository;

    public ImportStatusService(ImportFileRepository importFileRepository) {
        this.importFileRepository = importFileRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRemoveRequired(ImportFile file) {
        file.setStatus(ImportStatus.REMOVE_REQUIRES);
        importFileRepository.save(file);
    }
}
