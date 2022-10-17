package org.example.network.account.service.account;

import org.example.network.account.api.service.account.AccountService;
import org.example.network.account.configuration.AccountApplicationProperties;
import org.example.network.account.domain.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryingAccountServiceTest {

    @InjectMocks
    private RetryingAccountService retryingAccountService;

    @Mock
    private AccountApplicationProperties properties;

    @Mock
    private AccountService delegate;

    @Test
    void transferCallsMultipleTimesOnLockResult() {
        when(delegate.transfer(Mockito.any()))
                .thenReturn(OperationResponse.builder().status(Status.LOCK_EXCEPTION).build());
        when(properties.getOperationTimeoutInMilliseconds())
                .thenReturn(2000L);
        OperationResponse transfer = retryingAccountService.transfer(TransferRequest.builder().build());
        verify(delegate, atLeast(2)).transfer(Mockito.any());
        assertEquals(Status.TIMEOUT, transfer.getStatus());
    }

    @Test
    void depositCallsMultipleTimesOnLockResult() {
        when(delegate.deposit(Mockito.any()))
                .thenReturn(OperationResponse.builder().status(Status.LOCK_EXCEPTION).build());
        when(properties.getOperationTimeoutInMilliseconds())
                .thenReturn(2000L);
        OperationResponse transfer = retryingAccountService.deposit(DepositRequest.builder().build());
        verify(delegate, atLeast(2)).deposit(Mockito.any());
        assertEquals(Status.TIMEOUT, transfer.getStatus());
    }

    @Test
    void createAccountCallsMultipleTimesOnLockResult() {
        when(delegate.createAccount(Mockito.any()))
                .thenReturn(OperationResponse.builder().status(Status.LOCK_EXCEPTION).build());
        when(properties.getOperationTimeoutInMilliseconds())
                .thenReturn(2000L);
        OperationResponse transfer = retryingAccountService.createAccount(AccountCreationRequest.builder().build());
        verify(delegate, atLeast(2)).createAccount(Mockito.any());
        assertEquals(Status.TIMEOUT, transfer.getStatus());
    }
}