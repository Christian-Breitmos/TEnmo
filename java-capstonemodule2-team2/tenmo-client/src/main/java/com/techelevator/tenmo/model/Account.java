package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private int accountId;
    @JsonProperty("user_id")
    private int userId;
    private BigDecimal balance;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, balance);
    }

    @Override
    public String toString() {
        return "\n-----------------------------------" +
                "\n Account " + accountId +
                "\n User id " + userId +
                "\n Balance " + balance;
    }
}
