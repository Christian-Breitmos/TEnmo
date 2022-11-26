package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer createTransfer(int id, Transfer transfer);

    Transfer getTransfer(int id);

    List<Transfer> listTransfers();

    List<Transfer> listTransfersForUser(int id);

    void updateTransfer(int id, Transfer transfer);
}
