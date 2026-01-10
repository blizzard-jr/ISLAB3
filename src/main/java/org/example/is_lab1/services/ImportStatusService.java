package org.example.is_lab1.services;

import org.example.is_lab1.models.entity.ImportFile;
import org.example.is_lab1.models.entity.ImportStatus;
import org.example.is_lab1.repository.ImportFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportStatusService {
    ImportFileRepository importFileRepository;

    public ImportStatusService(ImportFileRepository importFileRepository) {
        this.importFileRepository = importFileRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(ImportFile importFile, String error) {
        importFile.setStatus(ImportStatus.FAILED);
        importFile.setErrorMessage(error);
        importFileRepository.save(importFile);
    }
}
