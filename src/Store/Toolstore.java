package Store;

import java.sql.*;
import java.util.Scanner;

public class Toolstore extends Storestart {

	public Toolstore(String loggedInID) {
		super(loggedInID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Scanner sc) {
		 Connection conn = null;
		
		 
		 try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe",
			         "seocho_DCX_DB_p1_3", "smhrd3");
			   System.out.println("======== 농기구 구매 ========\n");
               System.out.println("어떤 농기구를 구매하시겠습니까?\n");
               System.out.println("[1] 삽 [2] 괭이 [3] 호미");
               int num2 = sc.nextInt();
               sc.nextLine(); // Consume newline

               String toolType = ""; // 농기구 종류를 저장할 변수
               int price = 0; // 농기구 가격을 저장할 변수

               switch (num2) {
                   case 1:
                       toolType = "삽";
                       price = 650; // 예시 가격
                       break;
                   case 2:
                       toolType = "괭이";
                       price = 800; // 예시 가격
                       break;
                   case 3:
                       toolType = "호미";
                       price = 1000; // 예시 가격
                       break;
                   default:
                       System.out.println("잘못된 선택입니다.");
                       return; // 잘못된 선택일 경우 메서드를 종료합니다.
               }

            // 트랜잭션 시작

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
                               conn.rollback(); // 트랜잭션 롤백
                               return;
                           }
                       }
                   }

                   if (currentMoney >= price) {
                       // MONEY 값을 업데이트하기 위한 SQL 쿼리
                       String updateMoneySql = "UPDATE USERS SET MONEY = MONEY - ? WHERE ID = ?";
                       int moneyResult;
                       try (PreparedStatement updateMoneyPsmt = conn.prepareStatement(updateMoneySql)) {
                           updateMoneyPsmt.setInt(1, price); // 차감할 돈
                           updateMoneyPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                           moneyResult = updateMoneyPsmt.executeUpdate(); // SQL 쿼리 실행
                           
                           
                       }

                       if (moneyResult > 0) {
                           // 농기구 수량 업데이트
                           String updateToolSql = "UPDATE WAREHOUSE SET TOOL_QUANTITY = TOOL_QUANTITY + ? WHERE ID = ? AND TOOL_TYPE = ?";
                           try (PreparedStatement updateToolPsmt = conn.prepareStatement(updateToolSql)) {
                               updateToolPsmt.setInt(1, 1); // 추가할 수량
                               updateToolPsmt.setString(2, loggedInID); // 로그인된 사용자의 ID 설정
                               updateToolPsmt.setString(3, toolType); // 선택한 농기구 종류 설정
                               int toolResult = updateToolPsmt.executeUpdate(); // SQL 쿼리 실행
                               if (toolResult > 0) {
                                   System.out.println(toolType + "이(가) 추가되었습니다.");
                                   System.out.println("현재 잔액은 : " +   (currentMoney-price) +"\n");
                                   conn.commit(); // 트랜잭션 커밋
                               } else {
                                   System.out.println("농기구 수량 업데이트에 실패하였습니다.");
                                   conn.rollback(); // 트랜잭션 롤백
                               }
                           }
                       } else {
                           System.out.println("돈 차감에 실패하였습니다.");
                           conn.rollback(); // 트랜잭션 롤백
                       }
                   } else {
                       System.out.println("잔액이 부족합니다.");
                   }
               } catch (SQLException e) {
                   e.printStackTrace(); // SQL 예외가 발생한 경우 예외 스택 트레이스 출력
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		 
	}

}
