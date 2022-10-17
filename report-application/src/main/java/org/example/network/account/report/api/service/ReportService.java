package org.example.network.account.report.api.service;

import org.example.network.account.report.model.dto.AccountReport;
import org.example.network.account.report.model.dto.MoneyTransfer;
import org.springframework.scheduling.annotation.Scheduled;

public interface ReportService {

    void save(MoneyTransfer moneyTransfer);

    AccountReport getReport(String reportId);

    String createReport(String accountId);

    @Scheduled(cron = "0 0/10 * * * ?")
    void createReport();

    void removeOldReports();
}
