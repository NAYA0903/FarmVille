package JDBC;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Seeds {

	public static void main(String[] args) {
		
	
//	 2 - 1 씨앗 구매
//     1. 회원은 구매할 씨앗을 선택한다.
		
		PreparedStatement psmt = null ;
		ResultSet rs = null ;
		Connection conn = null ;
	
	
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver") ;
		
		String url =  "jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe";
		String user = "seocho_DCX_DB_p1_3" ;
		String password = "smhrd3";
		
		conn = DriverManager.getConnection(url, user, password);
		
		String sql = "SELECT * FROM CROPS" ;
		
		psmt = conn.prepareStatement(sql) ;
		
		rs = psmt.executeQuery() ;
		
		
		System.out.println("=========== 씨앗 ========= ");
		System.out.println(" 이름    가격    성장시간   경험치");
		  while (rs.next()) {
              System.out.println(rs.getString("CROP_NAME") + "\t"
                               + rs.getInt("CROP_PRICE") + "\t"
                               + rs.getInt("GROWTH_TIME") + "\t"
                               + rs.getInt("XP"));
          }
		  
//     2. 회원은 수량을 입력하고 구매 버튼을 클릭한다. .
		  
		Scanner sc = new Scanner(System.in) ;
	
		System.out.println("어떤 씨앗을 구매하시겠습니까?");
		System.out.println("[1] 당근  [2] 감자  [3] 토마토  [4] 양파  [5] 고구마");
		int num = sc.nextInt() ;
		 String seedType = "";
         int cou = 0;
         
		switch(num) {
        case 1:
           
            break;
        case 2:
            seedType = "감자";
            break;
        case 3:
            seedType = "토마토";
            break;
        case 4:
            seedType = "양파";
            break;
        case 5:
            seedType = "고구마";
            break;
        default:
            System.out.println("잘못된 입력입니다.");
            sc.close();
            return;
    }
//     3. 시스템은 결제를 처리하고 씨앗을 회원의 재고에 추가

		System.out.println("\n" + seedType + " 씨앗을 몇 개 구매하시겠습니까?");
        cou = sc.nextInt();
        System.out.println("\n구매가 완료되었습니다.");

        // 현재 SEED_QUANTITY 값을 조회
        String selectSql = "SELECT SEED_QUANTITY FROM WAREHOUSE WHERE SEED_TYPE = ?";
        psmt = conn.prepareStatement(selectSql);
        psmt.setString(1, seedType); 	
        
        rs = psmt.executeQuery();
        
        int currentQuantity = 0;
        if (rs.next()) {
            // 현재 SEED_QUANTITY 값을 가져옴
            currentQuantity = rs.getInt("SEED_QUANTITY");
        }
        
        // 기존 수량에 새로 구매한 수량을 더함
        int newQuantity = currentQuantity + cou;
        
        // SEED_QUANTITY 값을 업데이트
        String updateSql = "UPDATE WAREHOUSE SET SEED_QUANTITY = ? WHERE SEED_TYPE = ?";
        PreparedStatement updatePsmt = conn.prepareStatement(updateSql);
        updatePsmt.setInt(1, newQuantity);
        updatePsmt.setString(2, seedType);
        
        int result = updatePsmt.executeUpdate();
        
        if (result > 0) {
            System.out.println(seedType + "이 " + newQuantity + "개가 되었습니다.");
        } else {
            System.out.println("인터넷 통신이 좋지 않습니다. \n 다시 시도해 주세요");
        }
		
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		try {
			if(psmt != null)
				psmt.close(); 
			if(rs != null)
				rs.close() ;
			if(conn != null)
				conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		}
	}
 }
}
