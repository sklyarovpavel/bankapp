package org.example.network.account.report.api.repository;

import org.example.network.account.report.model.entity.TransferEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransferRepository {

    List<TransferEntity> findAllByAccountNumber(String accountNumber);

    void saveTransfer(TransferEntity transferEntity);

}
