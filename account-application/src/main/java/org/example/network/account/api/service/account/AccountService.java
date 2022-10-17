package org.example.network.account.api.service.account;

import org.example.network.account.domain.dto.*;

public interface AccountService {

    /**
     * @param request
     * @return
     */
    OperationResponse transfer(TransferRequest request);

    /**
     * @param request
     * @return
     */
    OperationResponse deposit(DepositRequest request);

    /**
     * @param request
     * @return
     */
    OperationResponse createAccount(AccountCreationRequest request);
}
