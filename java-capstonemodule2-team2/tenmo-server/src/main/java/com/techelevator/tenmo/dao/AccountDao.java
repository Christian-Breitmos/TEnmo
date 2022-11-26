package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal balance(int id);

    Account getAccount(int id);

     void updateAccount(int id, Account account);

     Account getAccountById(int id);
}
