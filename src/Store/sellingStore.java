package Store;

import java.sql.*;
import java.util.Scanner;

public class sellingStore extends Storestart {

    // 생성자에서 로그인된 사용자 ID를 부모 클래스에 전달
    public sellingStore(String loggedInID) {
        super(loggedInID);
    }

    @Override
    public void start(Scanner sc) {
        Connection conn = null;
        try {
            // 데이터베이스 연결
            conn = DriverManager.getConnection("jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe",
                    "seocho_DCX_DB_p1_3", "smhrd3");

            // 농작물 판매 옵션 안내
            System.out.println("================== 농작물 판매 ==================\n");
            System.out.println("어떤 농작물을 판매하시겠습니까? ");
            System.out.println("[1] 당근  [2] 감자  [3] 토마토  [4] 양파  [5] 고구마");
            int num = sc.nextInt(); // 사용자가 입력한 번호를 읽어옴
            sc.nextLine(); // 입력 버퍼에 남아있는 개행 문자 제거

            String cropsType = ""; // 씨앗 종류를 저장할 변수
            int price = 0; // 씨앗 가격을 저장할 변수

            // 사용자가 입력한 번호에 따라 씨앗 종류 및 가격 결정
            switch (num) {
                case 1:
                    cropsType = "당근";
                    price = 40;
                    break;
                case 2:
                    cropsType = "감자";
                    price = 60;
                    break;
                case 3:
                    cropsType = "토마토";
                    price = 80;
                    break;
                case 4:
                    cropsType = "양파";
                    price = 100;
                    break;
                case 5:
                    cropsType = "고구마";
                    price = 120;
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    return; // 잘못된 입력 시 메서드 종료
            }

            try {
                conn.setAutoCommit(false); // 트랜잭션 시작

                // 현재 MONEY 값을 조회하기 위한 SQL 쿼리
                String selectSql = "SELECT MONEY FROM USERS WHERE ID = ?";
                int currentMoney = 0;

                try (PreparedStatement selectPsmt = conn.prepareStatement(selectSql)) {
                    selectPsmt.setString(1, loggedInID); // 로그인된 사용자의 ID 설정

                    try (ResultSet rs = selectPsmt.executeQuery()) {
                        if (rs.next()) {
                            currentMoney = rs.getInt("MONEY"); // 현재 돈 조회
                            System.out.println("현재 잔액은 " + currentMoney + "입니다.\n");
                        } else {
                            System.out.println("사용자 정보를 찾을 수 없습니다.");
                            conn.rollback(); // 사용자 정보가 없으면 트랜잭션 롤백
                            return;
                        }
                    }
                }

                // 판매할 개수 입력
                System.out.print("판매할 개수를 입력하세요: ");
                int quantity = sc.nextInt(); // 사용자가 입력한 개수
                sc.nextLine(); // 입력 버퍼에 남아있는 개행 문자 제거

                // 작물 수량을 조회하기 위한 SQL 쿼리
                String checkCropSql = "SELECT CROP_QUANTITY FROM WAREHOUSE WHERE ID = ? AND CROP_TYPE = ?";
                int cropQuantity = 0;

                try (PreparedStatement checkCropPsmt = conn.prepareStatement(checkCropSql)) {
                    checkCropPsmt.setString(1, loggedInID); // 로그인된 사용자의 ID 설정
                    checkCropPsmt.setString(2, cropsType); // 선택한 작물 종류 설정

                    try (ResultSet rs = checkCropPsmt.executeQuery()) {
                        if (rs.next()) {
                            cropQuantity = rs.getInt("CROP_QUANTITY"); // 현재 작물 수량 조회
                            if (cropQuantity < quantity) {
                                System.out.println("보유한 작물 수량이 부족합니다.\n");
                                conn.rollback(); // 작물 수량 부족 시 트랜잭션 롤백
                                return;
                            }
                        } else {
                            System.out.println("해당 작물이 존재하지 않습니다.\n");
                            conn.rollback(); // 작물이 존재하지 않을 때 트랜잭션 롤백
                            return;
                        }
                    }
                }

                int totalPrice = price * quantity; // 총 가격 계산

                if (currentMoney >= totalPrice) {
                    // MONEY 값을 업데이트하기 위한 SQL 쿼리
                    String updateMoneySql = "UPDATE USERS SET MONEY = MONEY + ? WHERE ID = ?";
                    int result;

                    try (PreparedStatement updateMoneyPsmt = conn.prepareStatement(updateMoneySql)) {
                        updateMoneyPsmt.setInt(1, totalPrice); // 더해줄 돈
                        updateMoneyPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                        result = updateMoneyPsmt.executeUpdate(); // SQL 쿼리 실행
                    }

                    if (result > 0) {
                        // 작물 수량 업데이트
                        String updatecropSql = "UPDATE WAREHOUSE SET CROP_QUANTITY = CROP_QUANTITY - ? WHERE ID = ? AND CROP_TYPE = ?";

                        try (PreparedStatement updatecropPsmt = conn.prepareStatement(updatecropSql)) {
                            updatecropPsmt.setInt(1, quantity); // 업데이트할 작물 수량 설정
                            updatecropPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                            updatecropPsmt.setString(3, cropsType); // 선택한 작물 종류 설정

                            int cropResult = updatecropPsmt.executeUpdate(); // SQL 쿼리 실행

                            if (cropResult > 0) {
                                System.out.println(cropsType + "을 " + quantity + "개 판매 완료하였습니다.\n");
                                System.out.println("현재 잔액은 : " + (currentMoney + totalPrice) + "\n");
                                conn.commit(); // 트랜잭션 커밋
                            } else {
                                System.out.println("작물 수량 업데이트에 실패하였습니다.");
                                conn.rollback(); // 작물 수량 업데이트 실패 시 트랜잭션 롤백
                            }
                        }
                    } else {
                        System.out.println("돈 저장에 실패하였습니다.");
                        conn.rollback(); // 돈 저장 실패 시 트랜잭션 롤백
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
