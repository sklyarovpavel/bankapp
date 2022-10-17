package org.example.network.account.api.service.report;

import org.example.network.account.domain.dto.DepositRequest;
import org.example.network.account.domain.dto.TransferRequest;

public interface ReportServiceClient {

    /**
     * @param request
     */
    void sendReport(TransferRequest request);

    /**
     * @param request
     */
    void sendReport(DepositRequest request);
}
