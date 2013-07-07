package ckl.lrc;

public class LrcSentence {
	private String lrc;
	private long time;
	
	public LrcSentence(long time) {
		this.time = time;
	}
	
	public LrcSentence(String lrc) {
		this.lrc = lrc;
	}
	
	public LrcSentence(String lrc, long time) {
		this.lrc = lrc;
		this.time = time;
	}
	
	public String getLrc() {
		return lrc;
	}
	
	public void setLrc(String lrc) {
		this.lrc = lrc;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "["+time+"]" + lrc;
	}
}
