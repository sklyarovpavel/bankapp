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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

    private final  ReportRepository reportRepository;

    private final TransferRepository transferRepository;

    @Override
    public void save(MoneyTransfer moneyTransfer) {
        TransferEntity transferEntity = new TransferEntity();
        transferEntity.setAmount(moneyTransfer.getAmount());
        transferEntity.setId(moneyTransfer.getId());
        transferEntity.setAccountId(moneyTransfer.getId());
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
                .creationTime(OffsetDateTime.now())
                .updateTime(OffsetDateTime.MIN)
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
                List<TransferEntity> transfers = transferRepository.findAllByAccountNumber(entity.getAccountNumber());
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
