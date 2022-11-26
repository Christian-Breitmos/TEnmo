package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private final UserService userService = new UserService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// getBalance method is part of this file, scroll down to see it
        // gets the balance from the current user.
        getBalance(currentUser.getUser().getId());

	}

	private void viewTransferHistory() {
		// To view your transfer history we first need to know your account
        // we get your account by doing getAccount(your user  id), this method is found by scrolling down on this page
        Account account = getAccount(currentUser.getUser().getId());
        // after we get your account we are now able to get your account id
        //list transfers works by getting your account id, because account has a foreign key into account, user has a foreign key in account.
        // the listTransfers method is found at the bottom of this file
        listTransfers(account.getAccountId());
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        //this isnt fully functional but how it works is if the transfer status id is pending that means it's a request so it displays the user,
        // depending on what the user puts in it changes the status and type.
        Account account = userService.getAccount(currentUser.getUser().getId());
        Transfer[] transfers = userService.transfersForUser(account.getAccountId(), 1);
        consoleService.printRequests(transfers, currentUser.getUser().getId());

	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        //list the users and usernames of all users in the sql
        handleUsers();

        //enter the persons id that you want to send money too
        int id = consoleService.promptForReceiving();
        if (id == 0) {
            consoleService.printMainMenu();
        } else if (id == currentUser.getUser().getId()) {
            System.out.println("You can't send money to yourself silly");

        }
        else {
            //change ask you for how much money you want to send
            //change means amount of money
            BigDecimal change = consoleService.promptForChange();
            // itll now transfer the money from your id, to the user id, of the number you put in
            //transfermoney is apart of this file so scroll down to see it
            transferMoney(id, change, currentUser.getUser().getId());
            Account currentUserAccount = getAccount(currentUser.getUser().getId());
            consoleService.printBalance(currentUserAccount);
        }


	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        //this is almost fuctional just a couple bugs, same concept as send bucks but the transfertype id and status are different numbers

        handleUsers(); //List Users
        int idSending = consoleService.promptForInt("Enter ID of the user that you want money from: ");
        BigDecimal change = consoleService.promptForChange();
        userService.requestMoney(currentUser.getUser().getId(), change, idSending );
        Account accountRequesting = userService.getAccount(currentUser.getUser().getId());
        Account accountSending = userService.getAccount(idSending);
        handleAddTransfer(1, 1, accountRequesting.getAccountId(), accountSending.getAccountId(), change);


        }




    private void handleUsers() {
        User[] users = userService.listUsers();

        if (users != null) {
            consoleService.printUsersMenu(users, currentUser.getUser().getId());
        }
        else {
            System.out.println("empty");
        }
    }

    private void getBalance(int id) {
        //To see the balance you have to get your accountt by user_id
        Account account = userService.getAccount(id);

            consoleService.printBalance(account);

    }

    private Account getAccount(int id) {
        Account account = userService.getAccount(id);

        if (account != null) {
            return account;
        }
        else {
            return null;
        }
    }

    private void transferMoney(int receivingId, BigDecimal change, int sendingId) {
        //get the accounts of the users passed in so we change update their balance
        Account account = userService.getAccount(receivingId);
        Account accountSending = userService.getAccount(sendingId);

        //itll only update the transfer histroy if the statement returns true
        //go to user service to see how transferMoney updates the accounts
        boolean success = userService.transferMoney(receivingId, change, sendingId);

        if (success) {
            handleAddTransfer(2, 2, accountSending.getAccountId(), account.getAccountId(), change);
            System.out.println("Success!");
        }
        else {
            System.out.println("Failed!");
        }

    }

    private void listTransfers(int id) {
        //Create an array of transfers made by userService . transfers For user id
        //User service is its own file so go to userService to see how transfersForUser works
        Transfer[] transfers = userService.transfersForUser(id, 2);

        //in the console service itll print the transfers in the format that the readme wanted it.
        //look in console service to view printtransfers method
        //also for printtransfers to work we have to pass in the list we made of transfers and also pass in the currentUserId so we only see our transfer history
        consoleService.printTransfers(transfers, currentUser.getUser().getId());
    }

    private void handleAddTransfer(int transferTypeId, int transferStatusId, int accountFromId, int accountToId, BigDecimal amount) {

         Transfer transfer = new Transfer();
         transfer.setTransferTypeId(transferTypeId);
         transfer.setTransferStatusId(transferStatusId);
         transfer.setAccountFromId(accountFromId);
         transfer.setAccountToId(accountToId);
         transfer.setAmount(amount);

         userService.addTransfer(transfer);
    }

}
