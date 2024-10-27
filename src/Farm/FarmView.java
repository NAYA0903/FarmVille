package Farm;

import java.util.Scanner;

public class FarmView {
    private Scanner sc = new Scanner(System.in);

    public int showFarmMenu() {
        System.out.println("농장 메뉴");
        System.out.println("[1] 씨앗 심기 [2] 수확하기 [3] 돌아가기");
        System.out.print(">> ");
        return sc.nextInt();
    }

    public int showSeedOptions() {
        System.out.println("어떤 씨앗을 심으시겠습니까?");
        System.out.println(" [1] 당근 [2] 감자 [3] 양파 [4] 토마토 [5] 고구마 ");
        System.out.print(">> ");
        return sc.nextInt();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String getUserInput() {
        return sc.nextLine();
    }

    public void showWaitMessage() {
        System.out.println("씨앗을 심는 중입니다. 잠시만 기다려주세요...");
    }

    public String showHarvestConfirmation() {
        System.out.print("수확하시겠습니까? (y/n): ");
        return sc.next() ;
    }
    
    public int getIntInput() {
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


