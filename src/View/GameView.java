package View;

import java.util.Scanner;

public class GameView {
	
	//사용자와 상호작용하며 정보를 표시
	
	private Scanner sc;

    public GameView() {
        sc = new Scanner(System.in);
    }
    
    
    public int loginMenu() {
    	System.out.println(" [1] 회원가입 [2] 로그인 [3] 종료 ");
    	System.out.print(">> ");
        return sc.nextInt();
    }
    
    
    
    public int showMenu() {
        System.out.println(" [1] 농장 [2] 상점 [3] 오락관 [4] 로그아웃 ");
        System.out.print(">> ");
        return sc.nextInt();
    }
    
    public int showGameMenu() {
        System.out.println("[1] 가위바위보 [2] 숫자맞추기 [3] Up-down");
        System.out.print(">> ");
        return sc.nextInt();
    }

    public int getAmount() {
        
    	System.out.println("\n");
    	System.out.print("금액을 입력하세요 (최소 10원이상): ");
        return sc.nextInt();
        
    }

    public int getUserNumber() {
    	System.out.println("\n");
        System.out.print("1부터 10까지의 숫자를 입력하세요: ");
        return sc.nextInt();
    }

    public void displayResult(boolean isWin, int amount, int randomNumber) {
        if (isWin) {
            int win = amount * 10;
            System.out.println("\n");
            System.out.println("축하합니다! 숫자를 맞추셨습니다. " + amount + "원의 10배인 " + win + "원을 받으실 수 있습니다.");
            System.out.println("정답은 : " + randomNumber + "이었습니다!");
            System.out.println("\n");
        } else {
        	System.out.println("\n");
        	System.out.println("저런 .. 틀렸습니다. 입력하신 금액 " + amount + "원을 모두 잃으셨습니다.");
        	System.out.println("정답은 : " + randomNumber + "이었습니다!");
            System.out.println("\n");
        }
    }

    public char askToContinue() {
        System.out.print("다시 시작하시겠습니까? (y/n): ");
        return sc.next().toLowerCase().charAt(0);
    }

    public int getBettingAmount() {
        System.out.print("배팅할 금액을 입력해주세요 (최소 10원 이상): ");
        return sc.nextInt();
    }

    public int getUserNumber1() {
        System.out.println("1부터 49까지의 숫자를 입력하세요:");
        return sc.nextInt();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
    
    public String getUserChoice() {
        System.out.print("가위, 바위, 보 중 하나를 입력하세요: ");
        return sc.next();
    }
} 
