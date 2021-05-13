package banking;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static long card_number = 0;
    public static long card_code = 0;

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            Scanner scn = new Scanner(System.in);
            int option = scn.nextInt();
            if (option == 0) {
                System.out.println("Bye!");
                return;
            } else if (option == 1) {
                System.out.println("Your card has been created");
                System.out.println("Your card number:");
                Random random = new Random();
                card_number = 4_000_000_000_000_000L + (long) (random.nextDouble() * 999_999_9999L);
                System.out.println(card_number);
                System.out.println("Your card PIN:");
                card_code = 1000 + (long) (random.nextDouble() * 8999L);
                System.out.println(card_code);
            } else {
                System.out.println("Enter your card number:");
                long code = scn.nextLong();
                System.out.println("Enter your PIN:");
                long pin = scn.nextLong();
                if (code == card_number && pin == card_code) {
                    System.out.println("You have successfully logged in!");
                    while (true) {
                        System.out.println("1. Balance");
                        System.out.println("2. Log out");
                        System.out.println("0. Exit");
                        int choice = scn.nextInt();
                        if (choice == 1) {
                            System.out.println("Balance: 0");
                        } else if (choice == 2) {
                            System.out.println("You have successfully logged out!");
                            break;
                        } else {
                            return;
                        }
                    }
                } else {
                    System.out.println("Wrong card number or PIN!");
                }
            }
        }
    }
}