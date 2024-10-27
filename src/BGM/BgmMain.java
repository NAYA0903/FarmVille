package BGM;

import java.util.ArrayList;
import java.util.Scanner;

import javazoom.jl.player.MP3Player;


public class BgmMain {

	public static void main(String[] args) {
		
		ArrayList<BGM> musicList = new ArrayList<BGM>();
		Scanner sc = new Scanner(System.in);
		
		String comPath = "C:\\Users\\smhrd\\Desktop\\Projectsong";
		
		BGM m1 = new BGM("C:\\Users\\smhrd\\Desktop\\Projectsong\\Basic.mp3", "Basic");
		musicList.add(m1);
		
		musicList.add(new BGM(comPath + "Arcade.m4a", "Arcade"));
		
		// 노래의 인덱스를 저장하는 변수
		int i = 0;
		
		// 플레이어 만들기
		MP3Player mp3 = new MP3Player();
		
		if (!musicList.isEmpty()) {
            BGM firstSong = musicList.get(0);
            mp3.play(firstSong.getPath());
        } else {
            System.out.println("No songs available to play.");
        }

	}

}
