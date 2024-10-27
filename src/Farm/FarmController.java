package Farm;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Farm.FarmModel.Crop;
import Farm.FarmModel.User;

public class FarmController {
    private User user;
    private FarmView view;
    private Map<String, Crop> crops = new HashMap<>();
    private final Map<Integer, String> seedOptions = new HashMap<>();
    private Connection conn;
    private boolean isSeedPlanted = false;
    private String plantedSeedType = null;
    private int plantedSeedQuantity = 0;

    public FarmController(User user, FarmView view) {
        this.user = user;
        this.view = view;
        this.conn = initializeDatabaseConnection();
        if (this.view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        seedOptions.put(1, "당근씨");
        seedOptions.put(2, "감자씨");
        seedOptions.put(3, "양파씨");
        seedOptions.put(4, "토마토씨");
        seedOptions.put(5, "고구마씨");
    }

    private Connection initializeDatabaseConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe",
                    "seocho_DCX_DB_p1_3",
                    "smhrd3");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("데이터베이스 연결 실패");
        }
    }

    public void loadCropsFromDatabase() {
        System.out.println("============ 농사꾼 " + user.getId() + " 정보 ============");
        String query = "SELECT SEED_TYPE, SUM(SEED_QUANTITY) AS TOTAL_QUANTITY " +
                       "FROM WAREHOUSE " +
                       "WHERE ID = ? " +
                       "AND SEED_TYPE IS NOT NULL " +
                       "GROUP BY SEED_TYPE";

        try (PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, user.getId());
            try (ResultSet rs = psmt.executeQuery()) {
                crops.clear();
                while (rs.next()) {
                    String type = rs.getString("SEED_TYPE");
                    int quantity = rs.getInt("TOTAL_QUANTITY");

                    if (quantity > 0) {
                        crops.put(type, new Crop(type, quantity, 0));
                        System.out.println(" 보유 씨앗 종류 : " + type + ", 보유 수량 : " + quantity);
                    }
                }

                if (crops.isEmpty()) {
                    System.out.println("보유한 씨앗이 없습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getCropTypeFromSeed(String seedType) {
        switch (seedType) {
            case "당근씨": return "당근";
            case "감자씨": return "감자";
            case "양파씨": return "양파";
            case "토마토씨": return "토마토";
            case "고구마씨": return "고구마";
            default: return null;
        }
    }
    
    private String getSeedTypeFromCrop(String cropType) {
        switch (cropType) {
            case "당근": return "당근씨";
            case "감자": return "감자씨";
            case "양파": return "양파씨";
            case "토마토": return "토마토씨";
            case "고구마": return "고구마씨";
            default: return null;
        }
    }

    public void loadRealCropsFromDatabase() {
        System.out.println("============ 농사꾼 " + user.getId() + " 정보 ============");
        String query = "SELECT CROP_TYPE, SUM(CROP_QUANTITY) AS TOTAL_QUANTITY " +
                       "FROM WAREHOUSE " +
                       "WHERE ID = ? " +
                       "AND CROP_TYPE IS NOT NULL " +
                       "GROUP BY CROP_TYPE";

        try (PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, user.getId());
            try (ResultSet rs = psmt.executeQuery()) {
                crops.clear();
                while (rs.next()) {
                    String type = rs.getString("CROP_TYPE");
                    int quantity = rs.getInt("TOTAL_QUANTITY");

                    if (quantity > 0) {
                        crops.put(type, new Crop(type, quantity, 0));
                        System.out.println(" 보유 작물 종류 : " + type + ", 보유 수량 : " + quantity);
                    }
                }

                if (crops.isEmpty()) {
                    System.out.println("보유하고 있는 작물이 없습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleFarmMenu() {
        loadCropsFromDatabase();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n");
            System.out.println("농장 메뉴");
            System.out.println("[1] 씨앗 심기 [2] 수확하기 [3] 돌아가기");
            System.out.print(">> ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleSeedPlanting();
                    break;
                case 2:
                    handleHarvesting();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private void handleSeedPlanting() {
        Scanner sc = new Scanner(System.in);

        System.out.println("어떤 씨앗을 심으시겠습니까?");
        System.out.println("[1] 당근 [2] 감자 [3] 양파 [4] 토마토 [5] 고구마");
        System.out.print(">> ");

        int seedChoice = sc.nextInt();
        sc.nextLine(); // Consume newline

        String seedType = null;
        switch (seedChoice) {
            case 1: seedType = "당근씨"; break;
            case 2: seedType = "감자씨"; break;
            case 3: seedType = "양파씨"; break;
            case 4: seedType = "토마토씨"; break;
            case 5: seedType = "고구마씨"; break;
            default: System.out.println("잘못된 선택입니다."); return;
        }

        // 씨앗 타입을 작물 타입으로 변환
        this.plantedSeedType = getCropTypeFromSeed(seedType);

        System.out.print("심을 " + seedType + "의 개수를 입력하세요: ");
        int quantity = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.println(seedType + "씨앗을 심기 전에 물을 줍니다.");
        System.out.println("\n");
        System.out.println("씨앗을 심는 중입니다. 잠시만 기다려주세요...");
        System.out.println("\n");

        this.isSeedPlanted = true;
        this.plantedSeedQuantity = quantity;

        System.out.println("수확하시겠습니까? (y/n): ");
        String harvestChoice = sc.nextLine();
        if (harvestChoice.equalsIgnoreCase("y")) {
            handleHarvesting();
        } else {
            System.out.println("농장 메뉴로 돌아갑니다.");
        }
    }

    private void updateSeedQuantity(String seedType, int quantityToDeduct) {
        String selectSql = "SELECT SEED_QUANTITY FROM WAREHOUSE WHERE SEED_TYPE = ? AND ID = ?";
        int currentQuantity = 0;

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, seedType);
            selectStmt.setString(2, user.getId());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    currentQuantity = rs.getInt("SEED_QUANTITY");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.showMessage("씨앗 수량 조회 오류.");
            return;
        }

        int newQuantity = currentQuantity - quantityToDeduct;

        if (newQuantity < 0) {
            view.showMessage("씨앗 수량이 부족합니다.");
            return;
        }

        String updateSql = "UPDATE WAREHOUSE SET SEED_QUANTITY = ? WHERE SEED_TYPE = ? AND ID = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, newQuantity);
            updateStmt.setString(2, seedType);
            updateStmt.setString(3, user.getId());
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            view.showMessage("씨앗 수량 업데이트 오류.");
        }
    }
    
    private void updateCropQuantity(String seedType, int quantityToAdd) {
        String cropType = getCropTypeFromSeed(seedType); // 씨앗 타입에 맞는 작물 타입을 얻기
        if (cropType == null) {
            view.showMessage("작물 타입을 찾을 수 없습니다.");
            return;
        }

        String selectSql = "SELECT CROP_QUANTITY FROM WAREHOUSE WHERE CROP_TYPE = ? AND ID = ?";
        int currentQuantity = 0;
        boolean cropExists = false;

        try {
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 현재 작물 수량 조회
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, cropType);
                selectStmt.setString(2, user.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        currentQuantity = rs.getInt("CROP_QUANTITY");
                        cropExists = true;
                    }
                }
            }

            // 새로운 수량 계산
            int newQuantity = currentQuantity + quantityToAdd;

            // 작물 수량 업데이트 또는 삽입
            String updateSql = cropExists ?
                "UPDATE WAREHOUSE SET CROP_QUANTITY = ? WHERE CROP_TYPE = ? AND ID = ?" :
                "INSERT INTO WAREHOUSE (CROP_TYPE, CROP_QUANTITY, ID) VALUES (?, ?, ?)";

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                if (cropExists) {
                    updateStmt.setInt(1, newQuantity);
                    updateStmt.setString(2, cropType);
                    updateStmt.setString(3, user.getId());
                } else {
                    updateStmt.setString(1, cropType);
                    updateStmt.setInt(2, newQuantity);
                    updateStmt.setString(3, user.getId());
                }
                int rowsAffected = updateStmt.executeUpdate();
                System.out.println("작물 수량 업데이트 성공, 영향을 받은 행 수: " + rowsAffected);
            }

            conn.commit(); // 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            view.showMessage("농작물 수량 업데이트 오류.");

            try {
                if (conn != null) conn.rollback(); // 롤백
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
                view.showMessage("트랜잭션 롤백 오류.");
            }
        } finally {
            try {
                conn.setAutoCommit(true); // 자동 커밋 모드로 복원
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleHarvesting() {
        if (isSeedPlanted && plantedSeedQuantity > 0) {
            System.out.println(plantedSeedType + "을(를) 수확합니다!");
            System.out.println(plantedSeedQuantity + "개의 " + plantedSeedType + "을(를) 수확했습니다!");

            // 씨앗 타입을 찾는 로직
            String seedType = getSeedTypeFromCrop(plantedSeedType);
            if (plantedSeedType == null) {
                System.out.println("작물 타입을 찾을 수 없습니다.");
                return;
            }

            // 씨앗과 작물 수량 업데이트
            updateSeedQuantity(seedType, plantedSeedQuantity);
            updateCropQuantity(plantedSeedType, plantedSeedQuantity);

            // 데이터베이스 새로고침
            loadCropsFromDatabase();
            loadRealCropsFromDatabase();

            // 상태 초기화
            this.isSeedPlanted = false;
            this.plantedSeedType = null;
            this.plantedSeedQuantity = 0;
        } else {
            System.out.println("수확할 씨앗이 없습니다. 씨앗을 먼저 심어주세요.");
        }

        System.out.println("농장 메뉴로 돌아갑니다.");
    }

    private void pauseForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            view.showMessage("대기 중 오류 발생: " + e.getMessage());
        }
    }
}
