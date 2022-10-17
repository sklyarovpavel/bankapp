package org.example.network.account.api.service.conversion;

import java.math.BigDecimal;
import java.util.Currency;

public interface ConversionServce {

    /**
     * @param amount
     * @param destinationCurrency
     * @return
     */
    BigDecimal convert(BigDecimal amount, String destinationCurrency);

}
