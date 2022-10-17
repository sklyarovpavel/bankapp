package org.example.network.account.service.account;

import org.example.network.account.domain.dto.DepositRequest;
import org.example.network.account.domain.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultAccountServiceTest {

    private DefaultAccountService defaultAccountService;

    @Test
    void transfer() {
        String sourceAccountId = UUID.randomUUID().toString();
        String secondAccountId = UUID.randomUUID().toString();
        TransferRequest request = TransferRequest.builder().sourceAccountId(sourceAccountId)
                .destinationAccountId(secondAccountId)
                .amount(new BigDecimal(300)).currency("USD").build();
        defaultAccountService.transfer(request);
    }

    @Test
    void deposit() {
        String sourceAccountId = UUID.randomUUID().toString();
        String secondAccountId = UUID.randomUUID().toString();
        DepositRequest request = DepositRequest.builder().accountId(sourceAccountId)
                .amount(new BigDecimal(300)).currency("USD").build();
        defaultAccountService.deposit(request);
    }

    @Test
    void createAccount() {
    }
}