package com.techelevator.tenmo.services;


import com.fasterxml.jackson.core.JsonToken;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printUsersMenu(User[] users, int currentId) {

        // -------------------------------------------
        //Users
        //ID          Name
        //-------------------------------------------
        //313         Bernice
        //54          Larry
        //---------
        //
        //Enter ID of user you are sending to (0 to cancel):
        //Enter amount:


        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");

        for (User user : users) {
            if (user.getId() != currentId)
            System.out.println(user.getId() + "          " + user.getUsername());
        }
    }



    public void printTransfers(Transfer[] transfers, int currentUserId) {
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          From/To                 Amount");
        System.out.println("-------------------------------------------");

        //We have to implement user service here so we can get the commands, probably stems from not the cleanest coding but it works so.

        UserService userService = new UserService();

        for (Transfer transfer : transfers) {
            //Each transfer is connected to an account, and that account is connected to a user, and that user has a username
            //so we have to get the account by the transfer id, then get the user by the account id to print out the correct usernams
            Account account = userService.getAccountById(transfer.getAccountFromId());
            User fromUser = userService.getUserById(account.getUserId());
            Account toAccount = userService.getAccountById(transfer.getAccountToId());
            User toUser = userService.getUserById(toAccount.getUserId());

            // we only want to see what were not in kinda, so we dont need are username for this part. just the other username so we know what happened
            if (fromUser.getId() == currentUserId) {
                System.out.println(transfer.getTransferId() + "          " + "From:   " + toUser.getUsername() + "           $" + transfer.getAmount());
            } else {
                System.out.println(transfer.getTransferId() + "          " + "To: " + fromUser.getUsername() + "          $" + transfer.getAmount());
            }


        }

        //this was made by the team before us so i cant tell how promptforInt works but its in this file to see how it works.
        System.out.println("Please enter transfer Id to view details (0 to cancel) ");
        int num = promptForInt("Enter number: ");
        if (num != 0) {
            //print transfer is apart of this file so look at the method to see how each prints
            printTransfer(num);
        }
        printMainMenu();
    }

    public void printRequests(Transfer[] transfers, int id) {
        if (transfers.length == 0) {
            System.out.println("You currently no pending transfers. ");
        } else {
            System.out.println("-------------------------------------------");
            System.out.println("Requests");
            System.out.println("ID            From                 Amount");
            System.out.println("-------------------------------------------");

            //We have to implement user service here so we can get the commands, probably stems from not the cleanest coding but it works so.

            UserService userService = new UserService();


            for (Transfer transfer : transfers) {
                //Each transfer is connected to an account, and that account is connected to a user, and that user has a username
                //so we have to get the account by the transfer id, then get the user by the account id to print out the correct usernams
                if (transfer.getTransferStatusId() != 1 && transfer.getAccountFromId() == id) {
                    continue;
                }

                Account toAccount = userService.getAccountById(transfer.getAccountToId());
                User toUser = userService.getUserById(toAccount.getUserId());

                //print out the transfer id, who the request is from, and the amount requested

                System.out.println(transfer.getTransferId() + "          " + "From " + toUser.getUsername() + "          $" + transfer.getAmount());

            }

            System.out.println("Please enter transfer Id to view details (0 to cancel) ");
            int num = promptForInt("Enter number: ");
            if (num != 0) {
                //print transfer is apart of this file so look at the method to see how each prints
                printTransfer(num);
                Transfer updatingTransferRequest = userService.getTransfer(num);
                Account accountSending = userService.getAccountById(updatingTransferRequest.getAccountFromId());
                Account accountReceiving = userService.getAccountById(updatingTransferRequest.getAccountToId());
                String b = promptForString("Do you want to (A)ccept or (D)ecline the request: ");
                if (b.equalsIgnoreCase("A")) {

                    if (userService.transferMoney(accountSending.getUserId(), updatingTransferRequest.getAmount(), accountReceiving.getUserId())) {
                        updatingTransferRequest.setTransferStatusId(2);
                        userService.updateTransfer(updatingTransferRequest);
                    } else {
                        System.out.println("You don't have enough money");
                    }

                }

            }
        }

    }

    public boolean askUser() {
        //not used
        System.out.println("Insert if you want to do this transaction. T or F");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("t")) {
            return true;
        }
        return false;
    }

    public void printBalance(Account account) {
        System.out.println("-------------------------------------------");
        System.out.println("Your current balance is: $" + account.getBalance());
    }

    public void printAccount(Account account) {
        //not used
        System.out.println("----------------------------------");
        System.out.println(account);
    }


    public int promptForReceiving() {
        System.out.println("Enter ID of user you are sending to (0 to cancel): ");
        String num = scanner.nextLine();

        while (true) {
            try {
                int number = Integer.parseInt(num);
                return number;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID.");
            }
        }
    }

    public BigDecimal promptForChange() {
        System.out.println("Enter amount: ");
        String num = scanner.nextLine();

        while (true) {
            try {
                double number = Double.parseDouble(num);
                return BigDecimal.valueOf(number);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void printTransfer(int id) {
        //we passed in the id of the transfer the user wanted to look so we can get the transfer by  get Transfer By TransferId which is called transferById(id)
        System.out.println("You entered Transfer Id: " + id);
        Transfer transfer = null;

        //set the transfer to null so if it the user puts an invalid transfer id itll throw an error instead of crashing


            try {
               transfer = userService.transferById(id);

               //this was the same system as before with list transfers, probably due to poor coding format but it works.
               Account account = userService.getAccountById(transfer.getAccountFromId());
               User userFrom = userService.getUserById(account.getUserId());
               Account accountTo = userService.getAccountById(transfer.getAccountToId());
               User user = userService.getUserById(accountTo.getUserId());


                System.out.println();
                System.out.println();
                System.out.println("Transfer Details");
                System.out.println("--------------------------------------------");
                System.out.println("Id: " + transfer.getTransferId());
                System.out.println("From: " + user.getUsername() );
                System.out.println("To: " + userFrom.getUsername());
                System.out.println("Type: " + convertType(transfer.getTransferTypeId()));
                System.out.println("Status: " + convertStatus(transfer.getTransferStatusId()));
                System.out.println("Amount: $" + transfer.getAmount());


                pause();


            } catch (NullPointerException | NumberFormatException e) {
                System.out.println("Please enter a vaild number");
            }

    }

    public String convertStatus(int statusId) {
        String status;

        //if you look in the sql and put transfer_status each number stands for a status,
        // so you pass in the number of the status and it returns what each number represent

        if (statusId == 1) {
            return "Pending";
        } else if (statusId == 2) {
            return "Approved";
        } else {
            return "Rejected";
        }
    }

    public String convertType(int typeId) {


        //if you look in the sql and put transfer__type each number stands for a ty[e,
        // so you pass in the number of the type and it returns what each number represent

        if (typeId == 1) {
            return "Request";
        }
        return "Send";
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }


}
