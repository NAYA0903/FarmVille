package BGM;

public class BGM {

	private String title;
	
	// 노래 데이터의 위치(주소값,경로)
	private String path;
	
	// 메소드
	// 생성자, getter
	public BGM(String path, String title) {
		this.path = path;
		this.title = title;
	}
	
	public String getPath() {
		return path;		
	}
	
	public String getTitle() {
		return title;
	}	
	
}
