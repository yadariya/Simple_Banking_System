package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static long card_number = 0;
    public static long card_code = 0;

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:sqlite:" + args[1];
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            displayPrimaryMenu();
            Scanner scn = new Scanner(System.in);
            int option = scn.nextInt();
            if (option == 0) {
                System.out.println("Bye!");
                return;
            } else if (option == 1) {
                System.out.println("Your card has been created");
                System.out.println("Your card number:");
                Random random = new Random();
                long card_number_ = 400_000_000_000_000L + (long) (random.nextDouble() * 999_999_999L);
                card_number = Luhn_algorithm(card_number_);
                System.out.println(card_number);
                System.out.println("Your card PIN:");
                card_code = 1000 + (long) (random.nextDouble() * 8999L);
                System.out.println(card_code);
                String sql = "INSERT INTO card VALUES(?,?,?,?)";
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, Long.toString(card_number));
                    pstmt.setString(3, Long.toString(card_code));
                    pstmt.setInt(4, 0);
                    pstmt.executeUpdate();
                    //System.out.println("Everything is okay");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Enter your card number:");
                long code = scn.nextLong();
                System.out.println("Enter your PIN:");
                long pin = scn.nextLong();
                if (code == card_number && pin == card_code) {
                    System.out.println("You have successfully logged in!");
                    while (true) {
                        displaySecondaryMenu();
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
        System.out.println("2. Log out");
        System.out.println("0. Exit");
    }
}