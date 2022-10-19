package org.example.network.account.report.api.repository;

import org.example.network.account.report.model.entity.TransferEntity;

import java.util.List;

public interface TransferRepository {

    List<TransferEntity> findAllByAccountId(String accountId);

    void saveTransfer(TransferEntity transferEntity);

}
