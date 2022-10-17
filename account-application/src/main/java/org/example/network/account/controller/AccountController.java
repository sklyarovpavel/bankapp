package org.example.network.account.controller;

import lombok.RequiredArgsConstructor;
import org.example.network.account.api.service.account.AccountService;
import org.example.network.account.domain.dto.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
    
    private AccountService accountService;

    public OperationResponse transfer(TransferRequest request) {
        return accountService.transfer(request);
    }

    public OperationResponse deposit(DepositRequest request) {
        return accountService.deposit(request);
    }

    public OperationResponse createAccount(AccountCreationRequest request) {
        return accountService.createAccount(request);
    }
}
