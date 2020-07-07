package net.unit8.sillage.example.user.boundary;

import java.io.Serializable;

public class PromotionCreateRequest implements Serializable {
    private Long amount;
    private String currencyUnit;

    public PromotionCreateRequest(Long amount, String currencyUnit) {
        this.amount = amount;
        this.currencyUnit = currencyUnit;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }
}
