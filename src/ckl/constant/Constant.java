package ckl.constant;

import android.os.Environment;

public class Constant {
	public static final String HOST_ADDRESS = "http://192.168.1.100:8080/";
	public static final String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public interface PlayState {
		public static final int MPS_UNINIT = 0;				// 未就绪
		public static final int MPS_PREPARE = 1;			// 准备就绪(停止)
		public static final int MPS_PLAYING = 2;			// 播放中
		public static final int MPS_PAUSE = 3;				// 暂停
	}

	public interface PlayMsg {
		public static final int MSG_PLAY  = 0;
		public static final int MSG_PAUSE = 1;
		public static final int MSG_STOP  = 2;
	}
	
	private Constant() {}
}
