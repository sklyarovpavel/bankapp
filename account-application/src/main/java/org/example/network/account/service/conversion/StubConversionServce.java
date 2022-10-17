package org.example.network.account.service.conversion;

import org.example.network.account.api.service.conversion.ConversionServce;

import java.math.BigDecimal;

public class StubConversionServce implements ConversionServce {

    @Override
    public BigDecimal convert(BigDecimal amount, String destinationCurrency) {
        return amount;
    }

}
