package banking;

import org.sqlite.SQLiteDataSource;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static long card_number = 0;
    public static long card_code = 0;
    public static int balance = 0;

    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + args[1];
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        Database.create(dataSource);
        while (true) {
            displayPrimaryMenu();
            Scanner scn = new Scanner(System.in);
            int option = scn.nextInt();
            switch (option) {
                case 0:
                    System.out.println("Bye!");
                    return;
                case 1:
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:");
                    Random random = new Random();
                    long card_number_ = 400_000_000_000_000L + (long) (random.nextDouble() * 999_999_999L);
                    card_number = Luhn_algorithm(card_number_);
                    System.out.println(card_number);
                    System.out.println("Your card PIN:");
                    card_code = 1000 + (long) (random.nextDouble() * 8999L);
                    System.out.println(card_code);
                    Database.insert(dataSource);
                    break;
                case 2:
                    System.out.println("Enter your card number:");
                    long code = scn.nextLong();
                    String number = null;
                    String pin_ = null;
                    System.out.println("Enter your PIN:");
                    long pin = scn.nextLong();
                    String[] array = Database.selectNumberPin(dataSource, pin);
                    number = array[0];
                    pin_ = array[1];
                    if (number != null) {
                        card_number = Long.parseLong(number);
                        card_code = Long.parseLong(pin_);
                    }
                    if (Long.toString(code).equals(number) && Long.toString(pin).equals(pin_)) {
                        System.out.println("You have successfully logged in!");
                        while (true) {
                            displaySecondaryMenu();
                            int choice = scn.nextInt();
                            if (choice == 1) {
                                int balance_ = Database.selectBalance(dataSource);
                                System.out.println("Balance: " + balance_);
                            } else if (choice == 2) {
                                System.out.println("Enter income:");
                                int income = scn.nextInt();
                                Database.updateBalance(dataSource, income);
                                balance += income;
                                System.out.println("Income was added!\n");
                            } else if (choice == 3) {
                                System.out.println("Transfer");
                                System.out.println("Enter card number:");
                                long card_to_send = scn.nextLong();
                                String number_ = Database.selectNumber(dataSource, card_to_send);
                                if (!isLuhn(card_to_send)) {
                                    System.out.println("Probably you made a mistake in the card number. Please try again!");
                                } else if (number_ == null) {
                                    System.out.println("Such a card does not exist.");
                                } else {
                                    System.out.println("Enter how much money you want to transfer:");
                                    int moneyToTransfer = scn.nextInt();
                                    if (moneyToTransfer > balance) {
                                        System.out.println("Not enough money!");
                                    } else {
                                        Database.transaction(dataSource, moneyToTransfer, card_to_send);
                                        balance -= moneyToTransfer;
                                        System.out.println("Success!");
                                    }
                                }
                            } else if (choice == 4) {
                                System.out.println("The account has been closed!");
                                Database.delete(dataSource);
                                break;
                            } else if (choice == 5) {
                                System.out.println("You have successfully logged out!");
                                break;
                            } else {
                                System.out.println("Bye!");
                                return;
                            }
                        }
                    } else {
                        System.out.println("Wrong card number or PIN!");
                    }
            }
        }
    }

    public static boolean isLuhn(long l) {
        return Luhn_algorithm(l / 10) == l;
    }

    public static long Luhn_algorithm(long l) {
        String temp = Long.toString(l);
        int[] array = new int[temp.length()];
        int sum = 0;
        for (int i = 0; i < temp.length(); i++) {
            array[i] = temp.charAt(i) - '0';
            if (i % 2 == 0) {
                array[i] *= 2;
            }
            if (array[i] > 9) {
                array[i] -= 9;
            }
            sum += array[i];
        }
        int checksum = 0;
        for (int i = 1; i <= 9; i++) {
            if ((sum + i) % 10 == 0) {
                checksum = i;
                break;
            }
        }
        String result = Long.toString(l) + checksum;
        return Long.parseLong(result);
    }

    public static void displayPrimaryMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    public static void displaySecondaryMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }
}