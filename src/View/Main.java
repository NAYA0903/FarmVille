package View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;
import Controller.AuthService;
import Controller.GameController;
import Controller.MyController;
import Farm.FarmController;
import Farm.FarmModel.User;
import Farm.FarmStart;
import Farm.FarmView;
import Store.SeedStore;
import Store.Storestart;
import Store.Toolstore;
import Store.sellingStore;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Connection conn = null;
        
        String loggedInNickname = null ;

        // 데이터베이스 연결 설정
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe",
                    "seocho_DCX_DB_p1_3", "smhrd3");
        } catch (SQLException e) {
            e.printStackTrace();
            return; // 데이터베이스 연결 실패 시 프로그램 종료
        }

        // 회원가입 및 로그인 관련 클래스 불러오기
        AuthService authService = new AuthService(conn);
        // 출력문 실행 클래스 불러오기
        MyController controller = new MyController();
        
        FarmView view = new FarmView();

        // ==============================================================================================
        System.out.println("\n\n\n\n");
        // 스마트인재개발원 출력
        controller.Intro1();

        try {
            // 3000 밀리초(3초) 동안 대기
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n\n\n\n");
        // 시작 로고 출력
        controller.Intro2();

        System.out.println("\n\n\n\n\n\n");
        // ==============================================================================================

        boolean loggedIn = false; // 로그인 상태 추적 변수
        String loggedInID = null; // 로그인한 사용자 ID 저장

        while (true) {
            
         if (!loggedIn) {
                // 로그인 전
                System.out.println(" [1] 회원가입 [2] 로그인 [3] 종료 ");
                System.out.print(">> ");

                int choice = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (choice) {
                case 1:
                    // 회원가입
                    System.out.println("");
                    System.out.println("=========== 회원가입 ===========");
                    System.out.println("");

                    String signupID;
                    String signupPassword;
                    String signupNickname;
                    boolean signupSuccessful = false;

                    while (!signupSuccessful) {
                        System.out.print("등록하실 아이디를 입력하세요 : ");
                        signupID = sc.nextLine();
                        System.out.println("");

                        System.out.print("등록하실 비밀번호를 입력하세요 : ");
                        signupPassword = sc.nextLine();
                        System.out.println("");

                        System.out.print("등록하실 닉네임을 입력하세요 : ");
                        signupNickname = sc.nextLine();
                        System.out.println("");

                        signupSuccessful = authService.signup(signupID, signupPassword, signupNickname);
                        if (signupSuccessful) {
                            System.out.println("등록이 완료되었습니다!");

                            // WAREHOUSE 테이블에 초기 데이터 삽입
                            String[] seeds = { "당근씨", "감자씨", "토마토씨", "양파씨", "고구마씨" };
                            String[] crops = { "당근", "감자", "토마토", "양파", "고구마" };
                            String[] tools = { "삽", "괭이", "호미" };
                            

                            try {
                                // 트랜잭션 시작
                                conn.setAutoCommit(false);

                                // 씨앗 데이터 삽입
                                String seedSql = "INSERT INTO WAREHOUSE (ID, EXPERIENCE, SEED_TYPE, SEED_QUANTITY, CROP_TYPE, CROP_QUANTITY, TOOL_TYPE, TOOL_QUANTITY) VALUES (?, 0, ?, 0, NULL, 0, NULL, 0)";
                                try (PreparedStatement seedPsmt = conn.prepareStatement(seedSql)) {
                                    for (String seed : seeds) {
                                        seedPsmt.setString(1, signupID);
                                        seedPsmt.setString(2, seed);
                                        seedPsmt.executeUpdate();
                                    }
                                }
                                
                                // 농작물데이터
                                String cropSql = "INSERT INTO WAREHOUSE (ID, EXPERIENCE, SEED_TYPE, SEED_QUANTITY, CROP_TYPE, CROP_QUANTITY, TOOL_TYPE, TOOL_QUANTITY) VALUES (?, 0, NULL, 0, ?, 0, NULL, 0)";
                                try (PreparedStatement cropPsmt = conn.prepareStatement(cropSql)) {
                                    for (String crop : crops) {
                                        cropPsmt.setString(1, signupID);
                                        cropPsmt.setString(2, crop);
                                        cropPsmt.executeUpdate();
                                    }
                                }

                                // 농기구 데이터 삽입
                                String toolSql = "INSERT INTO WAREHOUSE (ID, EXPERIENCE, SEED_TYPE, SEED_QUANTITY, CROP_TYPE, CROP_QUANTITY, TOOL_TYPE, TOOL_QUANTITY) VALUES (?, 0, NULL, 0, NULL, 0, ?, 0)";
                                try (PreparedStatement toolPsmt = conn.prepareStatement(toolSql)) {
                                    for (String tool : tools) {
                                        toolPsmt.setString(1, signupID);
                                        toolPsmt.setString(2, tool);
                                        toolPsmt.executeUpdate();
                                    }
                                }

                                // 회원가입 시 MONEY 값을 500으로 추가하기 위한 SQL 쿼리
//                                String moneyUpdateSql = "UPDATE USERS SET MONEY =1 500 WHERE ID = ?";
//                                try (PreparedStatement moneyPsmt = conn.prepareStatement(moneyUpdateSql)) {
//                                    moneyPsmt.setString(1, signupID);
//                                    int moneyResult = moneyPsmt.executeUpdate();
//                                    if (moneyResult > 0) {
//                                        System.out.println("회원가입 축하드립니다! 500원이 계좌에 추가되었습니다.");
//                                    } else {
//                                        System.out.println("회원가입 후 MONEY 업데이트에 실패하였습니다.");
//                                        conn.rollback(); // 트랜잭션 롤백
//                                        continue;
//                                    }
//                                }

                                conn.commit(); // 트랜잭션 커밋
                            } catch (SQLException e) {
                                e.printStackTrace();
                                try {
                                    conn.rollback(); // 트랜잭션 롤백
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
                        } else {
                            System.out.println("등록을 실패하였습니다! 이미 존재하는 아이디입니다.");
                            System.out.println("다시 시도해주세요.");
                        }
                    }
                    break;

                    case 2:
                        // 로그인
                        System.out.println("=========== 로그인 ===========");
                        System.out.println("");

                        System.out.print("로그인 하실 ID를 입력하세요 : ");
                        String loginID = sc.nextLine();
                        System.out.println("");

                        System.out.print("로그인 하실 비밀번호를 입력하세요 : ");
                        String loginPassword = sc.nextLine();
                        System.out.println("");

                        String nickname = authService.login(loginID, loginPassword);
                        if (nickname != null) {
                            loggedIn = true;
                            loggedInID = loginID; // 사용자의 ID 저장
                            loggedInNickname = nickname;
                            System.out.println(nickname + "님, 로그인에 성공했습니다! 환영합니다.");
                            System.out.println("\n\n\n\n");

                            // ==============================================================================================================
                            String[] crops = {"당근", "감자", "토마토", "양파", "고구마"};
                            String[] plusJoke = {
                                "가(이) 태양빛을 잔뜩 머금었습니다!",
                                "에 재영쌤이 꿀을 바릅니다!",
                                "가(이) 오늘따라 때깔이 좋습니다!",
                                " 부족현상으로 가격이 올라갑니다!"
                            };
                            String[] minusJoke = {
                                "를(을) 미리쌤이 용납하지 않습니다!",
                                "를(을) 이수쌤이 대량 기부했습니다!",
                                "를(을) 태우쌤이 싫어합니다!",
                                "가(이) 넘쳐나서 가격이 떨어집니다!"
                            };
                            int[] price = {20, 30, 40, 50, 60};

                            Random rd = new Random();
                            int index = rd.nextInt(crops.length); // 랜덤 작물 선택
                            int index1 = rd.nextInt(plusJoke.length); // 상향 랜덤 구문
                            int index2 = rd.nextInt(minusJoke.length); // 하향 랜덤 구문

                            int amount = 10 + rd.nextInt(50); // 10~50의 값 생성

                            // 랜덤으로 가격이 오를지 내릴지 결정
                            boolean isPriceIncreasing = rd.nextBoolean();

                            // 원래 가격 = 랜덤 작물의 가격 
                            int firstPrice = price[index];
                            int newPrice;

                            if (isPriceIncreasing) {
                                // 가격 상승
                                newPrice = firstPrice + amount;
                                System.out.println(crops[index] + plusJoke[index1] + crops[index] + "의 가격이 " 
                                        + firstPrice + "원에서 " + newPrice + "원으로 " 
                                        + amount + "원 상승했습니다.");
                            } else {
                                // 가격 하락
                                int maxDecrease = Math.max(firstPrice / 2, 0); // 현재 가격의 50% 이내로 제한
                                newPrice = firstPrice - amount;

                                // 하락폭이 최소 가격 이하로 제한되도록 조정
                                if (newPrice < maxDecrease) {
                                    newPrice = maxDecrease;
                                }

                                // 가격이 0보다 낮아지지 않도록 조정
                                if (newPrice < 0) {
                                    newPrice = 0;
                                }
                                
                                System.out.println(crops[index] + minusJoke[index2] + crops[index] + "의 가격이 " 
                                        + firstPrice + "원에서 " + newPrice + "원으로 " 
                                        + (firstPrice - newPrice) + "원 하락했습니다.");
                            }

                            // 업데이트된 가격 반영
                            price[index] = newPrice;

                            System.out.println("\n\n\n");     // 업데이트된 가격
                            // ==================================================================================================

                        } else {
                            System.out.println("로그인에 실패하였습니다. 아이디 또는 비밀번호를 확인해주세요.");
                        }
                        break;

                    case 3:
                        System.out.println("프로그램을 종료합니다.");
                        try {
                            if (conn != null)
                                conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        sc.close();
                        return; // 프로그램 종료

                    default:
                        System.out.println("잘못된 선택입니다. 다시 시도해 주세요.");
                        break;
                }
            } else {
                // 로그인 후
                System.out.println(" [1] 농장 [2] 상점 [3] 오락관 [4] 로그아웃 ");
                System.out.print(">> ");

                int choice = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        // 농장 메뉴 선택
                        
                    	 FarmController farm = new FarmController(new User(loggedInID, 500), view);
                         farm.handleFarmMenu();
                        break;

                    case 2:
                        // 상점 메뉴 선택
                        System.out.println(" [1] 씨앗 구매 [2] 농기구 구매 [3] 농작물 판매 [4] 농기구 강화 [5] 뒤로가기 ");
                        System.out.print(">> ");
                        int num1 = sc.nextInt();
                        sc.nextLine(); // Consume newline

                        if (num1 == 1) {
                            Storestart seed = new SeedStore(loggedInID);
                            seed.start(sc);
                            break;
                        }

                        else if (num1 == 2) {
                            Storestart tool = new Toolstore(loggedInID) ;
                            tool.start(sc) ;
                            break ;
                        }
                               
                        else if (num1 == 3) {
                            Storestart sale = new sellingStore(loggedInID);
                            sale.start(sc) ;
                            break ;

                        } else if (num1 == 4) {
                            System.out.println("농기구 강화 기능은 현재 준비 중입니다.");
                        } else if (num1 == 5) {
                            // 뒤로가기
                            continue;
                        } else {
                            System.out.println("잘못된 선택입니다.");
                        }
                        break;

                    case 3:
                       System.out.println("[1] 가위바위보 [2] 숫자맞추기 [3] Up-down");
                        System.out.print(">> ");
                     
                        int game_choice = sc.nextInt() ;
                        sc.nextLine();
                        
                        GameController gameController = new GameController(loggedInID, conn);
                        
                        switch (game_choice) {
                            case 1:
                                gameController.startGame();
                                break; 
                           
                            case 2:
                                gameController.FindNumber();
                                break;
                           
                            case 3:
                                gameController.UpDown();
                                break;
                        }
                        
                        break;
                     
                     case 4:
                         // 로그아웃
                         loggedIn = false;
                         loggedInNickname = null;
                         System.out.println("로그아웃되었습니다.");
                         break;

                     default:
                         System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
                         break;
                 }
             }
         }
     }
 }
