package net.gringrid.imgoing.vo;

public class SpinnerVO {
	public String mName = null;
	public String mCode = null;
	
	public SpinnerVO() {
		
	}
	
	/**
	 * 스피터 형태의 아이템
	 * @param code 코드 밸류
	 * @param name 코드 네임
	 */
	public SpinnerVO(String code, String name) {
		this.mCode = code;
		this.mName = name;
	}
}
