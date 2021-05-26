package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;

import static banking.Main.card_code;
import static banking.Main.card_number;
//import static banking.Main.balance;

public class Database {
    public static void create(SQLiteDataSource dataSource) {
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
    }

    public static void insert(SQLiteDataSource dataSource) {
        String sql = "INSERT INTO card VALUES(?,?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, Long.toString(card_number));
            pstmt.setString(3, Long.toString(card_code));
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete(SQLiteDataSource dataSource) {
        String deleteFrom = "DELETE FROM card WHERE pin = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteFrom)) {
            // set the corresponding param
            pstmt.setString(1, Long.toString(card_code));
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void transaction(SQLiteDataSource dataSource, int moneyToTransfer, long card_to_send) {
        String updateDown = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String updateUp = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (Connection connection = dataSource.getConnection()
        ) {
            connection.setAutoCommit(false);
            try (PreparedStatement down = connection.prepareStatement(updateDown);
                 PreparedStatement up = connection.prepareStatement(updateUp)) {
                //update down
                down.setInt(1, moneyToTransfer);
                down.setString(2, Long.toString(card_number));
                down.executeUpdate();
                //update up
                up.setInt(1, moneyToTransfer);
                up.setString(2, Long.toString(card_to_send));
                up.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBalance(SQLiteDataSource dataSource, int income) {
        String updateOrigin = "UPDATE card SET balance = balance + ? WHERE pin = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateOrigin)) {
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, Long.toString(card_code));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String selectNumber(SQLiteDataSource dataSource, long card_to_send) {
        String number_ = null;
        String getCode = "SELECT number FROM card WHERE number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(getCode)
        ) {
            pstmt.setString(1, Long.toString(card_to_send));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                number_ = rs.getString("number");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return number_;
    }

    public static int selectBalance(SQLiteDataSource dataSource) {
        int balance = 0;
        String getBalance = "SELECT balance FROM card WHERE number=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(getBalance)) {
            pstmt.setString(1, Long.toString(card_number));
            // loop through the result set
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    public static String[] selectNumberPin(SQLiteDataSource dataSource, long pin) {
        String[] array = new String[2];
        String getPinCode = "SELECT number, pin FROM card WHERE pin = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(getPinCode)
        ) {
            pstmt.setString(1, Long.toString(pin));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                array[0] = rs.getString("number");
                array[1] = rs.getString("pin");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return array;
    }
}
