package ckl.constant;

import android.os.Environment;

public interface Constant {
	String HOST_ADDRESS = "http://192.168.1.100:8080/";
	String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
	String ACTION_LRC_UPDATE = "mp3player.lrc.update";

	interface PlayState {
		int MPS_UNINIT = 0;				// 未就绪
		int MPS_PREPARE = 1;			// 准备就绪(停止)
		int MPS_PLAYING = 2;			// 播放中
		int MPS_PAUSE = 3;				// 暂停
	}

	interface PlayMsg {
		int MSG_PLAY  = 0;
		int MSG_PAUSE = 1;
		int MSG_STOP  = 2;
	}
}
