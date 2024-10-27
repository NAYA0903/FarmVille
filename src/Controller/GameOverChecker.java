package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GameOverChecker {
    private Connection conn;

    // 생성자에서 Connection을 초기화합니다.
    public GameOverChecker(Connection conn) {
        this.conn = conn;
    }

    // 사용자의 돈을 조회합니다.
    public int getUserMoney(String userId) {
        int money = 0;
        String sql = "SELECT MONEY FROM USERS WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    money = rs.getInt("MONEY");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return money;
    }

    // 사용자의 돈을 업데이트합니다.
    public void updateUserMoney(String userId, int newAmount) {
        String sql = "UPDATE USERS SET MONEY = ? WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newAmount);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자를 삭제하고 관련된 데이터를 제거합니다.
    public void deleteUser(String userId) {
        String sqlUser = "DELETE FROM WAREHOUSE WHERE ID = ?";
        String sqlWarehouse = "DELETE FROM USERS WHERE MONEY = 0";

        try {
            // 트랜잭션 자동 커밋 설정
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUser = conn.prepareStatement(sqlUser);
                 PreparedStatement pstmtWarehouse = conn.prepareStatement(sqlWarehouse)) {

                // 사용자 관련 창고 데이터 삭제
                pstmtUser.setString(1, userId); // userId 매개변수 설정
                pstmtUser.executeUpdate();

                // MONEY가 0인 모든 사용자 삭제
                pstmtWarehouse.executeUpdate();

                // 모든 쿼리가 성공적으로 실행되면 커밋
                conn.commit();
            } catch (SQLException e) {
                // 에러 발생 시 롤백
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 트랜잭션 자동 커밋으로 복원
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}

