package org.example.network.account.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.network.account.api.service.account.AccountService;
import org.example.network.account.configuration.AccountApplicationProperties;
import org.example.network.account.domain.dto.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class RetryingAccountService implements AccountService {

    private final AccountApplicationProperties properties;

    private final AccountService delegate;

    @Override
    public OperationResponse transfer(TransferRequest request) {
        return executeWithRetry(request, delegate::transfer);
    }

    @Override
    public OperationResponse deposit(DepositRequest request) {
        return executeWithRetry(request, delegate::deposit);
    }

    @Override
    public OperationResponse createAccount(AccountCreationRequest request) {
        return executeWithRetry(request, delegate::createAccount);
    }

    private <T> OperationResponse executeWithRetry(T request, Function<T, OperationResponse> function) {
        OffsetDateTime timeout = OffsetDateTime.now().plus(properties.getOperationTimeoutInMilliseconds(), ChronoUnit.MILLIS);
        while (OffsetDateTime.now().compareTo(timeout) < 0) {
            try {
                OperationResponse result = function.apply(request);
                if (!result.getStatus().equals(Status.LOCK_EXCEPTION)) {
                    return result;
                }
            } catch (Exception ex) {
                log.error("Account operation execution failed", ex);
            }
        }
        return timeout();
    }

    private OperationResponse timeout() {
        return OperationResponse.builder().status(Status.TIMEOUT).build();
    }
}
