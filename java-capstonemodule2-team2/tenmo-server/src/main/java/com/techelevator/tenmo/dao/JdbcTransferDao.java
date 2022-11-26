package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    private List<Transfer> transfers = new ArrayList<>();

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer createTransfer(int id, Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?); ";

        Integer newId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());


        return getTransfer(newId);
    }

    @Override
    public Transfer getTransfer(int id) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_id = ?; ";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }

        return transfer;

    }

    public List<Transfer> listTransfers() {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;
    }

    public List<Transfer> listTransfersForUser(int id) {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE account_from = ? or account_to = ? ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;
    }

    public void updateTransfer(int id, Transfer transfer) {

        //String sql = "UPDATE account SET account_id = ?, user_id = ?, balance = ? WHERE user_id = ?; ";
        //
        //        jdbcTemplate.update(sql, account.getAccountId(), account.getUser_id(), account.getBalance(), id);

        String sql = "UPDATE transfer SET transfer_id = ?, transfer_type_id = ?, transfer_status_id = ?, account_from = ?, account_to = ?, amount = ? WHERE transfer_id = ?; ";

        jdbcTemplate.update(sql, transfer.getTransferId(), transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount(), id);

    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setAccountFromId(rowSet.getInt("account_from"));
        transfer.setAccountToId(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));

        return transfer;

    }
}

