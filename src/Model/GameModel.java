package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import Controller.DatabaseManager;

public class GameModel {
    private Random rd;
    private int randomNumber;
    private int userMoney;
    
    private Connection conn;

    public GameModel(Connection conn) {
    this.conn = conn;
    
}

    public GameModel() {
    	
        rd = new Random();
        generateNewNumber();
    }

    public void generateNewNumber() {
        randomNumber = rd.nextInt(10) + 1;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public int getUserMoney(String userId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT MONEY FROM USERS WHERE ID = ?")) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userMoney = rs.getInt("MONEY");
                } else {
                    System.out.println("User not found: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userMoney;
    }

    public void updateUserMoney(String userId, int newAmount) {
        int updatedAmount = Math.max(newAmount, 0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE USERS SET MONEY = ? WHERE ID = ?")) {
            pstmt.setInt(1, updatedAmount);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUpdatedMoney() {
        return userMoney;
    }

    public void setUpdatedMoney(int amount) {
        this.userMoney = amount;
    }
}
