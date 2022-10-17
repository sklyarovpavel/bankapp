package org.example.network.account.service.account;

import lombok.RequiredArgsConstructor;
import org.example.network.account.api.repository.AccountRepository;
import org.example.network.account.api.service.account.AccountService;
import org.example.network.account.api.service.conversion.ConversionServce;
import org.example.network.account.api.service.report.ReportServiceClient;
import org.example.network.account.api.service.rounting.RepositoryRoutingService;
import org.example.network.account.domain.dto.*;
import org.example.network.account.domain.entity.AccountEntity;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final RepositoryRoutingService routingService;

    private final TransactionTemplate transactionTemplate;

    private final ConversionServce conversionServce;

    private final ReportServiceClient reportService;

    @Override
    public OperationResponse transfer(TransferRequest request) {
        String sourceAccountId = request.getSourceAccountId();
        AccountRepository sourceAccountRepository = routingService.getRepositoryNameByAccountId(sourceAccountId);
        AccountRepository destinationAccountRepository = routingService.getRepositoryNameByAccountId(request.getDestinationAccountId());
        if (sourceAccountRepository == null) {
            return sourceAccountNotFount();
        }
        if (destinationAccountRepository == null) {
            return destinationAccountNotFound();
        }
        AccountEntity sourceAccount = sourceAccountRepository.getAccount(sourceAccountId);
        AccountEntity destinationAccount = destinationAccountRepository.getAccount(sourceAccountId);
        BigDecimal destinationAmount = request.getAmount();
        BigDecimal sourceAmount = request.getAmount();
        String currency = request.getCurrency();
        if (!sourceAccount.getCurrency().equals(request.getCurrency())) {
            sourceAmount = conversionServce.convert(sourceAmount, currency);
        }
        if (!destinationAccount.getCurrency().equals(request.getCurrency())) {
            destinationAmount = conversionServce.convert(destinationAmount, currency);
        }
        BigDecimal newSourceAmount = sourceAccount.getAmount().subtract(sourceAmount);
        if (newSourceAmount.compareTo(BigDecimal.ZERO) < 0) {
            return insufficientFunds();
        }
        sourceAccount.setAmount(newSourceAmount);
        destinationAccount.setAmount(destinationAccount.getAmount().add(destinationAmount));
        return transactionTemplate.execute(status ->
                getOperationResponse(request, sourceAccountRepository, destinationAccountRepository, sourceAccount, destinationAccount));
    }

    private OperationResponse getOperationResponse(TransferRequest request,
                                                   AccountRepository sourceAccountRepository,
                                                   AccountRepository destinationAccountRepository,
                                                   AccountEntity sourceAccount, AccountEntity destinationAccount) {
        if (sourceAccountRepository.updateAccount(sourceAccount)) {
            if (destinationAccountRepository.updateAccount(destinationAccount)) {
                reportService.sendReport(request);
                return success();
            }
        }
        return lockException();
    }

    private OperationResponse destinationAccountNotFound() {
        return OperationResponse.builder().status(Status.DESTINATION_ACCOUNT_NOT_FOUND).build();
    }

    @Override
    public OperationResponse deposit(DepositRequest request) {
        String accountId = request.getAccountId();
        AccountRepository accountRepository = routingService.getRepositoryNameByAccountId(accountId);
        if (accountRepository == null) {
            return destinationAccountNotFound();
        }
        BigDecimal amount = request.getAmount();
        String currency = request.getCurrency();
        AccountEntity account = accountRepository.getAccount(accountId);
        if (!account.getCurrency().equals(request.getCurrency())) {
            amount = conversionServce.convert(amount, currency);
        }
        account.setAmount(account.getAmount().add(amount));
        return transactionTemplate.execute(status -> deposit(request, accountRepository, account));
    }

    private OperationResponse deposit(DepositRequest request, AccountRepository accountRepository, AccountEntity account) {
        if (accountRepository.updateAccount(account)) {
            reportService.sendReport(request);
            return success();
        }
        return lockException();
    }

    @Override
    public OperationResponse createAccount(AccountCreationRequest request) {
        String accountId = request.getAccountId();
        AccountRepository existing = routingService.getRepositoryNameByAccountId(request.getAccountId());
        if (existing != null) {
            return alreadyExists();
        }
        routingService.saveRouting(accountId);
        AccountRepository repositoryNameByAccountId = routingService.getRepositoryNameByAccountId(accountId);
        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setCurrency(request.getCurrency());
        account.setAmount(BigDecimal.ZERO);
        repositoryNameByAccountId.save(account);
        return success();
    }

    private OperationResponse alreadyExists() {
        return OperationResponse.builder().status(Status.ALREADY_EXISTS).build();
    }

    private OperationResponse sourceAccountNotFount() {
        return OperationResponse.builder().status(Status.SOURCE_ACCOUNT_NOT_FOUND).build();
    }

    private OperationResponse success() {
        return OperationResponse.builder().status(Status.SUCCESS).build();
    }

    private OperationResponse insufficientFunds() {
        return OperationResponse.builder().status(Status.SOURCE_ACCOUNT_INSUFFICIENT_FUNDS).build();
    }

    private OperationResponse lockException() {
        return OperationResponse.builder().status(Status.LOCK_EXCEPTION).build();
    }

}
