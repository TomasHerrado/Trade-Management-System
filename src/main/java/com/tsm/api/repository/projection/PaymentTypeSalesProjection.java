package com.tsm.api.repository.projection;

import com.tsm.api.entity.PaymentType;
import java.math.BigDecimal;

public interface PaymentTypeSalesProjection {
    PaymentType getPaymentType();
    BigDecimal getTotal();
}