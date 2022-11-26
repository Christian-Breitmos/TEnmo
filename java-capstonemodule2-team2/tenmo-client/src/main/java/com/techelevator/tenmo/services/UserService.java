package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.accessibility.AccessibleTable;
import java.math.BigDecimal;
import java.util.List;

public class UserService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void SetAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public User[] listUsers() {
        User[] users = null;

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }


    public User getUserById(int id) {
        //get user by user id
        User user = null;

        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "users/" + id, HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }


    //this is get account by user id
    public Account getAccount(int id) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "accounts/" + id, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Transfer getTransfer(int id) {
        Transfer transfer = null;

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    // this is get account by account id
    public Account getAccountById(int id) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "accounts/account/" + id, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public boolean transferMoney(int idReceiving, BigDecimal change, int idSending) {

        //just like the method before, get the user accounts so we can update their balance

        Account accountSending = getAccount(idSending);
        Account accountReceiving = getAccount(idReceiving);

        BigDecimal sendingBalance = accountSending.getBalance();
        BigDecimal receivingBalance = accountReceiving.getBalance();

        //set these variables above to each balance to so we can update them

        int test = sendingBalance.intValue();
        int changeInt = change.intValue();

        //bigdecimal math is very weird so I did to make sure they dont go below 0
        if (test - changeInt < 0) {
            System.out.println("You don't have enough money in your account to do this transaction");
            return false;
        }

        test = receivingBalance.intValue();

        if (test - changeInt < 0) {
            return false;
        }

        //now declare to new variables where you subtract the money from sending account and add the money to receiving account

        BigDecimal newSendingValue = sendingBalance.subtract(change);
        BigDecimal newReceivingValue = receivingBalance.add(change);


        //set each account balance to their new balances

        accountSending.setBalance(newSendingValue);
        accountReceiving.setBalance(newReceivingValue);

        //now update their accounts :D

        updateAccount(accountReceiving);
        updateAccount(accountSending);

        return true;

    }

    public boolean requestMoney(int idReceiving, BigDecimal change, int idSending) {

        //this doesnt work properly
        Account accountSending = getAccount(idSending);
        Account accountReceiving = getAccount(idReceiving);

        BigDecimal sendingBalance = accountSending.getBalance();
        BigDecimal receivingBalance = accountReceiving.getBalance();

        int test = sendingBalance.intValue();
        int changeInt = change.intValue();

        if (test - changeInt < 0) {
            return false;
        }

        test = receivingBalance.intValue();

        if (test - changeInt < 0) {
            return false;
        }

        return true;

    }

    public boolean updateAccount(Account updatedAccount) {
        boolean success = false;

        try {
            restTemplate.put(API_BASE_URL + "accounts/" + updatedAccount.getUserId(),
                    makeAccountEntity(updatedAccount));
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public void updateTransfer(Transfer updatedTransfer) {


        try {
            restTemplate.put(API_BASE_URL + "transfers/" + updatedTransfer.getTransferId(),
                    makeTransferEntity(updatedTransfer));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

    }

    public Transfer addTransfer(Transfer newTransfer) {
        //posting a transfer to the database
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, headers);

        Transfer returnedTransfer = null;

        try {
            returnedTransfer = restTemplate.postForObject(API_BASE_URL + "transfers", entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    public Transfer[] transfersForUser(int id, int requestId) {
        //First you want to set the list of transfers to null as we will be filling it in with this command below
        //if the server side works it'll return a new body
        Transfer[] transfers = null;

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfers/filter?id=" + id + "&type=" + requestId, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;

    }

    public Transfer transferById(int id) {
        //this gets the transfer by the transfer id
        Transfer transfer = null;

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;

    }

    public HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }

    public HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }



    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
