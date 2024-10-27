package Store;

import java.sql.*;
import java.util.Scanner;

public class SeedStore extends Storestart {

    public SeedStore(String loggedInID) {
        super(loggedInID); // 부모 클래스의 생성자를 호출
    }

    @Override
    public void start(Scanner sc) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe",
                    "seocho_DCX_DB_p1_3", "smhrd3");
            
            System.out.println("=========== 씨앗 구매 ===========");
            System.out.println(""); // 빈 줄 출력

            // 사용자가 구매할 씨앗 종류를 선택하도록 안내
            System.out.print("어떤 씨앗을 구매하시겠습니까? ");
            System.out.println("[1] 당근 : 20원  [2] 감자 : 30원  [3] 토마토 : 40원 [4] 양파 : 50원  [5] 고구마 : 60원 ");
            int num = sc.nextInt(); // 사용자가 입력한 번호를 읽어옴
            sc.nextLine(); // 입력 버퍼에 남아있는 개행 문자 제거

            String seedType = ""; // 씨앗 종류를 저장할 변수
            int price = 0; // 씨앗 가격을 저장할 변수

            // 사용자가 입력한 번호에 따라 씨앗 종류 및 가격 결정
            switch (num) {
                case 1:
                    seedType = "당근씨";
                    price = 20;
                    break;
                case 2:
                    seedType = "감자씨";
                    price = 30;
                    break;
                case 3:
                    seedType = "토마토씨";
                    price = 40;
                    break;
                case 4:
                    seedType = "양파씨";
                    price = 50;
                    break;
                case 5:
                    seedType = "고구마씨";
                    price = 60;
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    return; // 잘못된 입력 시 메서드 종료
            }

            try {
                conn.setAutoCommit(false);

                // 현재 MONEY 값을 조회하기 위한 SQL 쿼리
                String selectSql = "SELECT MONEY FROM USERS WHERE ID = ?";
                int currentMoney = 0;

                try (PreparedStatement selectPsmt = conn.prepareStatement(selectSql)) {
                    selectPsmt.setString(1, loggedInID); // 로그인된 사용자의 ID 설정

                    try (ResultSet rs = selectPsmt.executeQuery()) {
                        if (rs.next()) {
                            currentMoney = rs.getInt("MONEY"); // 현재 돈 조회
                            System.out.println("현재 잔액은 " + currentMoney + "입니다.");
                        } else {
                            System.out.println("사용자 정보를 찾을 수 없습니다.");
                            conn.rollback(); // 사용자 정보가 없으면 트랜잭션 롤백
                            return;
                        }
                    }
                }

                System.out.print("구매할 개수를 입력하세요: ");
                int quantity = sc.nextInt(); // 사용자가 입력한 개수
                sc.nextLine(); // 입력 버퍼에 남아있는 개행 문자 제거

                int totalPrice = price * quantity;

                if (currentMoney >= totalPrice) {
                    // MONEY 값을 업데이트하기 위한 SQL 쿼리
                    String updateMoneySql = "UPDATE USERS SET MONEY = MONEY - ? WHERE ID = ?";
                    int result;

                    try (PreparedStatement updateMoneyPsmt = conn.prepareStatement(updateMoneySql)) {
                        updateMoneyPsmt.setInt(1, totalPrice); // 차감할 돈
                        updateMoneyPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                        result = updateMoneyPsmt.executeUpdate(); // SQL 쿼리 실행
                    }

                    if (result > 0) {
                        // 씨앗 수량 업데이트
                        String updateSeedSql = "UPDATE WAREHOUSE SET SEED_QUANTITY = SEED_QUANTITY + ? WHERE ID = ? AND SEED_TYPE = ?";

                        try (PreparedStatement updateSeedPsmt = conn.prepareStatement(updateSeedSql)) {
                            updateSeedPsmt.setInt(1, quantity); // 업데이트할 씨앗 수량 설정
                            updateSeedPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                            updateSeedPsmt.setString(3, seedType); // 선택한 씨앗 종류 설정

                            int seedResult = updateSeedPsmt.executeUpdate(); // SQL 쿼리 실행

                            if (seedResult > 0) {
                                System.out.println(seedType + " 씨앗을 " + quantity + "개 구매 완료하였습니다.\n");
                                System.out.println("현재 잔액은 : " + (currentMoney - totalPrice) + "\n");
                                conn.commit(); // 트랜잭션 커밋
                            } else {
                                System.out.println("씨앗 수량 업데이트에 실패하였습니다.");
                                conn.rollback(); // 씨앗 수량 업데이트 실패 시 트랜잭션 롤백
                            }
                        }
                    } else {
                        System.out.println("돈 차감에 실패하였습니다.");
                        conn.rollback(); // 돈 차감 실패 시 트랜잭션 롤백
                    }
                } else {
                    System.out.println("잔액이 부족합니다.\n");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // SQL 예외 발생 시 예외 스택 트레이스 출력
                try {
                    conn.rollback(); // 예외 발생 시 트랜잭션 롤백
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                try {
                    conn.setAutoCommit(true); // 자동 커밋 모드로 복귀
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close(); // 데이터베이스 연결 종료
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
