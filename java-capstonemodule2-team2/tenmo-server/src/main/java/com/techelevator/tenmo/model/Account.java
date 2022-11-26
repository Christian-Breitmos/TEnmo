package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int accountId;
    private int user_id;
    private BigDecimal balance;

    public int getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "AccountId" + user_id +
                "UserId" + accountId +
                "Balance" + balance;
    }
}
