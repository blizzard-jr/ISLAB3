package org.example.is_lab1.services;

import lombok.extern.slf4j.Slf4j;
import org.example.is_lab1.models.entity.ImportOperation;
import org.example.is_lab1.models.entity.XaStatus;
import org.example.is_lab1.repository.ImportOperationRepository;
import org.example.is_lab1.transactions.SimpleXid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.xa.Xid;

@Service
@Slf4j
public class ApplicationXaManager {
    private final ImportOperationRepository importOperationRepository;

    public ApplicationXaManager(ImportOperationRepository importOperationRepository) {
        this.importOperationRepository = importOperationRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SimpleXid begin(ImportOperation operation) {
        SimpleXid xid = SimpleXid.random();
        operation.setXaXid(xid.asString());
        operation.setXaStatus(XaStatus.ACTIVE);
        importOperationRepository.save(operation);
        log.info("XA begin: op={} xid={}", operation.getId(), xid.asString());
        return xid;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SimpleXid prepare(Long operationId) {
        ImportOperation op = importOperationRepository.getReferenceById(operationId);
        SimpleXid xid = ensureXid(op);
        op.setXaStatus(XaStatus.PREPARED);
        importOperationRepository.save(op);
        log.info("XA prepare: op={} xid={}", op.getId(), xid.asString());
        return xid;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void commit(Xid xid, Long operationId) {
        ImportOperation op = importOperationRepository.getReferenceById(operationId);
        op.setXaStatus(XaStatus.COMMITTED);
        importOperationRepository.save(op);
        log.info("XA commit: op={} xid={}", operationId, op.getXaXid());
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollback(Xid xid, Long operationId, String reason) {
        ImportOperation op = importOperationRepository.getReferenceById(operationId);
        op.setXaStatus(XaStatus.ROLLED_BACK);
        importOperationRepository.save(op);
        log.warn("XA rollback: op={} xid={} reason={}", operationId, op.getXaXid(), reason);
    }

    public SimpleXid ensureXid(ImportOperation op) {
        if (op.getXaXid() == null || op.getXaXid().isBlank()) {
            return begin(op);
        }
        return SimpleXid.fromString(op.getXaXid());
    }
}
