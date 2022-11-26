package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TEnmoController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public TEnmoController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getListOfUsers() {
        // TODO change authentication
        return userDao.findAll();
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable int id) {
        Account account = accountDao.getAccount(id);
        return account;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.PUT)
    public void updateAccount(@Valid @RequestBody Account account, @PathVariable int id) {
        accountDao.updateAccount(id, account);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public void updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable int id) {
        transferDao.updateTransfer(id ,transfer);

        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer addTransfer(@Valid @RequestBody Transfer transfer) {
        return transferDao.createTransfer(transfer.getTransferId(), transfer);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "transfers", method = RequestMethod.GET)
    public List<Transfer> listTransfers() {

        return transferDao.listTransfers();
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/transfers/filter", method = RequestMethod.GET)
    public List<Transfer> listTransfersForUser(@RequestParam int id, @RequestParam(required = false, defaultValue = "2") Integer type) { //by account id

        List<Transfer> filteredTransfers = new ArrayList<>();
        List<Transfer> allTransfers = listTransfers();

        for (Transfer transfer : allTransfers) {

            if (type == 2) { // if transfer type equals 'receive'

                if (transfer.getAccountFromId() == id || transfer.getAccountToId() == id) {

                    filteredTransfers.add(transfer);


                }

            } else {
                if (transfer.getTransferTypeId() == 1) {

                    if (transfer.getAccountFromId() == id || transfer.getAccountToId() == id) {

                        filteredTransfers.add(transfer);

                    }
                }
            }
        }

        return filteredTransfers;

    }

//    @PreAuthorize("permitAll")
//    @RequestMapping(path = "/transfers/filter", method = RequestMethod.GET)
//    public List<Transfer> listRequestsForUser(@RequestParam int id, @RequestParam Integer status) { //by account id
//
//        List<Transfer> filteredTransfers = new ArrayList<>();
//        List<Transfer> allTransfers = listTransfers();
//
//        for (Transfer transfer : allTransfers) {
//
//           if (transfer.getTransferStatusId() == 1) {
//               if (transfer.getAccountToId() == 1 || transfer.getAccountFromId() == 1) {
//                   filteredTransfers.add(transfer);
//               }
//           }
//        }
//
//        return filteredTransfers;
//
//    }


    @PreAuthorize("permitAll")
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id) {

        return transferDao.getTransfer(id);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "users/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable int id) {
        User user = userDao.getUserById(id);
        return user;
    }


    @PreAuthorize("permitAll")
    @RequestMapping(path = "accounts/account/{id}", method = RequestMethod.GET)
    public Account getAccountByAccountId(@PathVariable int id) {
        return accountDao.getAccountById(id);
    }





    }




