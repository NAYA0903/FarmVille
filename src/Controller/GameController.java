package Controller;

import java.sql.Connection;
import java.util.Random;

import Model.GameModel;
import View.GameView;

public class GameController {
    private GameOverChecker over;
    private GameModel model;
    private GameView view;
    private String userId;
    private Connection conn;

    private static final int MIN_BET_AMOUNT = 10;

    public GameController(String userId, Connection conn) {
        this.over = new GameOverChecker(conn);
        this.view = new GameView();
        this.userId = userId;
        this.conn = conn;
        this.model = new GameModel(conn);  // GameModel 인스턴스 생성
    }
    
    private void checkGameOver() {
        int userMoney = over.getUserMoney(userId);
        if (userMoney <= 0) {
            view.showMessage("게임 오버! 당신의 MONEY가 0원이 되었습니다.");
            over.deleteUser(userId); // 사용자 탈퇴 처리
            System.out.println("사용자가 탈퇴되었습니다. 프로그램을 종료합니다.");
            System.exit(0); // 프로그램 종료
        }
    }
 
    public void FindNumber() {
        char re;
        int userMoney = over.getUserMoney(userId);

        do {
            int amount = view.getAmount();

            while (amount < MIN_BET_AMOUNT || amount > userMoney) {
                if (amount < MIN_BET_AMOUNT) {
                    System.out.println("배팅 금액은 최소 " + MIN_BET_AMOUNT + "원 이상이어야 합니다. 다시 입력해주세요.");
                } else {
                    System.out.println("입력한 금액이 소유 금액보다 많습니다. 다시 입력해주세요.");
                }
                amount = view.getAmount();
            }

            int userNumber = view.getUserNumber();
            while (userNumber < 1 || userNumber > 10) {
                System.out.println("1부터 10까지의 숫자만 입력하세요!");
                userNumber = view.getUserNumber();
            }

            int randomNumber = model.getRandomNumber();
            boolean isWin = (userNumber == randomNumber);

            if (isWin) {
                int win = amount * 10;
                userMoney += win;
                view.displayResult(true, amount, randomNumber);
            } else {
                userMoney -= amount;
                view.displayResult(false, amount, randomNumber);
            }

            model.updateUserMoney(userId, userMoney);
            checkGameOver(); // MONEY 확인 및 게임 오버 처리

            view.showMessage("현재 소유 금액: " + userMoney);
          
            if (!isWin) {
                re = view.askToContinue();
            } else {
                re = 'n';
            }

        } while (re == 'y');

        System.out.println("게임을 종료합니다.");
       // 게임 종료 후 메인 메뉴 호출
    }

    public void UpDown() {
        char re;
        do {
            System.out.println("\n");
            view.showMessage("업다운 게임 시작합니다~");
            System.out.println("\n\n");

            int userMoney = model.getUserMoney(userId);
            System.out.println("현재 소유 금액: " + userMoney);
            System.out.println("\n");

            int bettingAmount = getValidBettingAmount(userMoney);

            int comnum = generateRandomNumber();
            int attempts = 6;

            view.showMessage("숫자를 맞춰보세요! 기회는 총 6번입니다.");

            boolean isCorrect = playGame(comnum, attempts, bettingAmount);

            if (!isCorrect) {
                view.showMessage("아쉽게도 숫자를 맞추지 못했습니다. 배팅 금액 " + bettingAmount + "원을 잃었습니다.");
                view.showMessage("정답은 " + comnum + "이었습니다.");
            }

            userMoney = model.getUserMoney(userId);
            checkGameOver(); // MONEY 확인 및 게임 오버 처리

            re = view.askToContinue();

        } while (re == 'Y' || re == 'y');

        System.out.println("게임을 종료합니다.");
    }

    public void startGame() {
        char re;
        do {
            System.out.println("\n");
            view.showMessage("가위바위보를 시작합니다!");
            System.out.println("\n");
            int userMoney = model.getUserMoney(userId);
            System.out.println("현재 소유 금액: " + userMoney);

            int bettingAmount = getValidBettingAmount(userMoney);

            boolean isWin = playRockPaperScissors(bettingAmount);

            if (isWin) {
                System.out.println("\n");
                view.showMessage("축하합니다! 승리하셨습니다! 배팅금액의 2배인 " + (bettingAmount * 2) + "원을 받습니다.");
                System.out.println("\n");
                model.updateUserMoney(userId, userMoney + bettingAmount * 2);
            } else {
                System.out.println("\n");
                view.showMessage("아쉽게도 패배하셨습니다. 배팅 금액을 잃었습니다.");
                System.out.println("\n");
                model.updateUserMoney(userId, userMoney - bettingAmount);
            }

            userMoney = model.getUserMoney(userId);
            checkGameOver(); // MONEY 확인 및 게임 오버 처리

            view.showMessage("현재 소유 금액: " + userMoney);

            re = view.askToContinue();

        } while (re == 'Y' || re == 'y');

        System.out.println("\n");
        view.showMessage("게임을 종료합니다.");
        System.out.println("\n");
    }

    private int getValidBettingAmount(int userMoney) {
        int bettingAmount;
        do {
            bettingAmount = view.getBettingAmount();
            if (bettingAmount < MIN_BET_AMOUNT) {
                view.showMessage("배팅 금액은 최소 " + MIN_BET_AMOUNT + "원 이상이어야 합니다. 다시 입력해주세요.");
            } else if (bettingAmount > userMoney) {
                view.showMessage("입력한 금액이 소유 금액보다 많습니다. 다시 입력해주세요.");
            }
        } while (bettingAmount < MIN_BET_AMOUNT || bettingAmount > userMoney);
        return bettingAmount;
    }

    private int getValidUserNumber() {
        int userNumber;
        do {
            userNumber = view.getUserNumber();
            if (userNumber < 1 || userNumber > 10) {
                view.showMessage("1부터 10까지의 숫자만 입력하세요!");
            }
        } while (userNumber < 1 || userNumber > 10);
        return userNumber;
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(49) + 1;
    }

    private boolean playGame(int comnum, int attempts, int bettingAmount) {
        boolean isCorrect = false;
        int userMoney = model.getUserMoney(userId);

        while (bettingAmount < MIN_BET_AMOUNT || bettingAmount > userMoney) {
            if (bettingAmount < MIN_BET_AMOUNT) {
                view.showMessage("배팅 금액은 최소 " + MIN_BET_AMOUNT + "원 이상이어야 합니다. 다시 입력해주세요.");
            } else {
                view.showMessage("입력한 금액이 소유 금액보다 많습니다. 다시 입력해주세요.");
            }
            bettingAmount = view.getAmount();
        }

        for (int attempt = 1; attempt <= attempts; attempt++) {
            int num = view.getUserNumber1();

            if (num < comnum) {
                view.showMessage("업");
            } else if (num > comnum) {
                view.showMessage("다운");
            } else {
                isCorrect = true;
                view.showMessage("축하합니다! 숫자를 맞췄습니다!\n배팅금액의 2배 " + (bettingAmount * 2) + "원을 수령하였습니다");
                int win = bettingAmount * 2;
                userMoney += win;
                model.updateUserMoney(userId, userMoney);
                System.out.println("현재 소유 금액: " + userMoney);
                break;
            }

            if (attempt < attempts) {
                view.showMessage("틀렸습니다. 남은 기회는 : " + (attempts - attempt) + "번 입니다");
            } else {
                userMoney -= bettingAmount;
                model.updateUserMoney(userId, userMoney);
                System.out.println("아쉽게도 맞추지 못하여 " + bettingAmount + "원이 차감되었습니다.");
                System.out.println("현재 소유 금액: " + userMoney);
            }
        }
        return isCorrect;
    }

    private boolean playRockPaperScissors(int bettingAmount) {
        boolean isWin = false;

        while (!isWin) {
            String userChoice = view.getUserChoice();
            String comChoice = generateRockPaperScissorsChoice();
            String result = determineRockPaperScissorsResult(userChoice, comChoice);

            view.showMessage("사용자 선택: " + userChoice);
            view.showMessage("컴퓨터 선택: " + comChoice);
            view.showMessage("결과: " + result);

            if (result.equals("승리")) {
                isWin = true;
            } else if (result.equals("패배")) {
                return false;
            }
        }

        return isWin;
    }

    private String generateRockPaperScissorsChoice() {
        String[] choices = { "가위", "바위", "보" };
        Random random = new Random();
        return choices[random.nextInt(choices.length)];
    }

    private String determineRockPaperScissorsResult(String userChoice, String comChoice) {
        if (userChoice.equals(comChoice)) {
            return "비김";
        }

        if ((userChoice.equals("가위") && comChoice.equals("보")) ||
            (userChoice.equals("바위") && comChoice.equals("가위")) ||
            (userChoice.equals("보") && comChoice.equals("바위"))) {
            return "승리";
        } else {
            return "패배";
        }
    }
}
