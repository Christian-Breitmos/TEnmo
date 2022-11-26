package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal balance(int id) {
        Account account = null;

        String sql = "Select balance FROM account WHERE user_id = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapAccountToResults(results);

        }
        return account.getBalance();
    }

    @Override
    public Account getAccount(int id) {
        Account account = null;

        String sql = "Select user_id, account_id, balance FROM account WHERE user_id = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapAccountToResults(results);

        }

        return account;
    }

    @Override
    public void updateAccount(int id, Account account) {

        String sql = "UPDATE account SET account_id = ?, user_id = ?, balance = ? WHERE user_id = ?; ";

        jdbcTemplate.update(sql, account.getAccountId(), account.getUser_id(), account.getBalance(), id);

    }

    @Override
    public Account getAccountById(int id) {
        Account account = null;

        String sql = "Select user_id, account_id, balance FROM account WHERE account_id = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapAccountToResults(results);

        }

        return account;
    }


    private Account mapAccountToResults(SqlRowSet rs) {

        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUser_id(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));

        return account;
    }
}
