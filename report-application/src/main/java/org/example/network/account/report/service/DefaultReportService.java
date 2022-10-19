package org.example.network.account.report.service;

import lombok.RequiredArgsConstructor;
import org.example.network.account.report.api.repository.ReportRepository;
import org.example.network.account.report.api.service.ReportService;
import org.example.network.account.report.api.repository.TransferRepository;
import org.example.network.account.report.model.dto.AccountReport;
import org.example.network.account.report.model.dto.MoneyTransfer;
import org.example.network.account.report.model.dto.ReportStatus;
import org.example.network.account.report.model.entity.ReportEntity;
import org.example.network.account.report.model.entity.TransferEntity;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

    private final ReportRepository reportRepository;

    private final TransferRepository transferRepository;

    @Override
    public void save(MoneyTransfer moneyTransfer) {
        TransferEntity transferEntity = TransferEntity.builder()
                .amount(moneyTransfer.getAmount())
                .id(moneyTransfer.getId())
                .accountId(moneyTransfer.getId())
                .build();
        transferRepository.saveTransfer(transferEntity);
    }

    @Override
    public AccountReport getReport(String reportId) {
        return null;
    }

    @Override
    public String createReport(String accountId) {
        ReportEntity reportEntity = ReportEntity.builder()
                .reportStatus(ReportStatus.NOT_YET_READY)
                .creationTime(new Date())
                .updateTime(new Date(0))
                .accountNumber(accountId)
                .id(UUID.randomUUID().toString())
                .build();
        return reportEntity.getId();
    }

    @Override
    @Scheduled(cron = "0 0/1 * * * ?")
    public void createReport() {
        List<ReportEntity> unfinishedRequests = reportRepository.getUnfinishedRequests();
        for (ReportEntity entity : unfinishedRequests) {
            ReportEntity updated = reportRepository.getReportAndChangeUpdateTime(entity);
            if (updated != null) {
                List<TransferEntity> transfers = transferRepository.findAllByAccountId(entity.getAccountNumber());
                updated.setTransferList(transfers);
                updated.setReportStatus(ReportStatus.READY);
                reportRepository.save(updated);
                return;
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldReports() {
        reportRepository.deleteOld();
    }

}
