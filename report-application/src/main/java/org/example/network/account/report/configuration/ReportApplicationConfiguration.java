package org.example.network.account.report.configuration;

import org.example.network.account.report.api.repository.ReportRepository;
import org.example.network.account.report.api.repository.TransferRepository;
import org.example.network.account.report.api.service.ReportService;
import org.example.network.account.report.repository.MongoReportRepository;
import org.example.network.account.report.service.DefaultReportService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class ReportApplicationConfiguration {


   private ReportRepository reportRepository(MongoTemplate mongoTemplate,
                                             ReportApplicationConfigurationProperties properties) {
       return new MongoReportRepository(mongoTemplate, properties);
   }

    private ReportService reportService(ReportRepository reportRepository,
                                        TransferRepository transferRepository) {
        return new DefaultReportService(reportRepository, transferRepository);
    }


}
