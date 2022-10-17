package org.example.network.account.report.api.repository;

import org.example.network.account.report.model.entity.ReportEntity;
import org.example.network.account.report.model.entity.TransferEntity;

import java.util.List;

public interface ReportRepository {

    List<TransferEntity> findAllByAccountNumber(String accountNumber);

    List<ReportEntity> getUnfinishedRequests();

    ReportEntity getReportAndChangeUpdateTime(ReportEntity report);

    ReportEntity getReport(String reportId);

    void save(ReportEntity reportRequest);

    void deleteOld();
}
