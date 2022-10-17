package org.example.network.account.report.repository;

import lombok.RequiredArgsConstructor;
import org.example.network.account.report.api.repository.ReportRepository;
import org.example.network.account.report.api.repository.TransferRepository;
import org.example.network.account.report.configuration.ReportApplicationConfigurationProperties;
import org.example.network.account.report.model.dto.ReportStatus;
import org.example.network.account.report.model.entity.ReportEntity;
import org.example.network.account.report.model.entity.TransferEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MongoReportRepository implements ReportRepository, TransferRepository {

    private final MongoTemplate mongoTemplate;

    private final ReportApplicationConfigurationProperties configurationProperties;

    @Override
    public List<TransferEntity> findAllByAccountNumber(String accountNumber) {
        Criteria criteria = new Criteria("accountNumber").is(accountNumber);
        return mongoTemplate.find(new Query(criteria), TransferEntity.class);
    }

    @Override
    public void saveTransfer(TransferEntity transferEntity) {
        mongoTemplate.save(transferEntity);
    }

    @Override
    public List<ReportEntity> getUnfinishedRequests() {
        Criteria criteria = new Criteria("reportStatus").is(ReportStatus.NOT_YET_READY);
        return mongoTemplate.find(new Query(criteria), ReportEntity.class);
    }

    @Override
    public ReportEntity getReportAndChangeUpdateTime(ReportEntity report) {
        Criteria criteria = new Criteria("id").is(report.getId()).and("updateTime")
                .gte(OffsetDateTime.now().minusMinutes(configurationProperties.getReportProcessingTimeoutInMinutes()));
        Update update = new Update();
        update.set("updateTime", OffsetDateTime.now());
        return mongoTemplate.findAndModify(new Query(criteria), update, ReportEntity.class);
    }

    @Override
    public ReportEntity getReport(String reportId) {
        return mongoTemplate.findById(reportId, ReportEntity.class);
    }

    @Override
    public void save(ReportEntity report) {
        mongoTemplate.save(report);
    }

    @Override
    public void deleteOld() {
        Criteria criteria = new Criteria("reportStatus")
                .is(ReportStatus.READY)
                .and("updateTime")
                .lte(OffsetDateTime.now().minusDays(configurationProperties.getReportCleanupTimeoutInDays()));
        mongoTemplate.remove(new Query(criteria), ReportEntity.class);
    }
}
