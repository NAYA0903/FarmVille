package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private Connection connection;

    public AuthService(Connection connection) {
        this.connection = connection;
    }

    // 사용자 등록 메서드
    public boolean signup(String id, String pw, String nickname) {
        if (isUsernameExists(id)) {
            return false; // 아이디가 이미 존재함
        }

        String sql = "INSERT INTO users (ID, PW, NAME) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, pw);
            stmt.setString(3, nickname);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 아이디 중복 확인 메서드
    private boolean isUsernameExists(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 로그인 메서드 (닉네임 반환)
    public String login(String id, String pw) {
        String sql = "SELECT NAME FROM users WHERE ID = ? AND PW = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, pw);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("NAME");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 로그인 실패
    }

	public String getUserID(String loggedInNickname) {
		// TODO Auto-generated method stub
		return null;
	}
}
