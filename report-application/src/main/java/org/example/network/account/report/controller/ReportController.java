package org.example.network.account.report.controller;

import lombok.RequiredArgsConstructor;
import org.example.network.account.report.api.service.ReportService;
import org.example.network.account.report.model.dto.AccountReport;
import org.example.network.account.report.model.dto.MoneyTransfer;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    public void save(MoneyTransfer moneyTransfer) {
        reportService.save(moneyTransfer);
    }

    public String createReport(String accountId) {
        return reportService.createReport(accountId);
    }

    public AccountReport getReport(String reportId) {
        return reportService.getReport(reportId);
    }

}
