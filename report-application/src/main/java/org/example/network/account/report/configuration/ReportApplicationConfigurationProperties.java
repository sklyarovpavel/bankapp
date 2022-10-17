package org.example.network.account.report.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report")
public class ReportApplicationConfigurationProperties {

    private int reportProcessingTimeoutInMinutes = 10;

    private int reportCleanupTimeoutInDays = 10;

    private String unfinishedReportPollingQuartsExpression = "0 0/1 * * * ?";

    private String deleteTaskPollingQuartsExpression = "0 0/1 * * * ?";

}
