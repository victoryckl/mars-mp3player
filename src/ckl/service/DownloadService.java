package ckl.service;

import ckl.constant.Constant;
import ckl.model.Mp3Info;
import ckl.utils.HttpDownloader;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {
	private static final String TAG = "DownloadService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
//		Log.i(TAG, mp3Info.toString());
		Thread thread = new Thread(new DownloadRunnable(mp3Info));
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	class DownloadRunnable implements Runnable {
		private Mp3Info mp3Info = null;
		public DownloadRunnable(Mp3Info mp3Info) {
			this.mp3Info = mp3Info;
		}
		@Override
		public void run() {
			HttpDownloader downloader = new HttpDownloader();
			int result = downloader.downFile(
					Constant.HOST_ADDRESS + "mp3/" + mp3Info.getMp3Name(), 
					"mp3/", mp3Info.getMp3Name());
			String resultMsg = null;
			if (result == -1) {
				resultMsg = "下载失败！";
			} else if (result == 0) {
				resultMsg = "文件下载成功！";
			} else if (result == 1) {
				resultMsg = "文件已经存在，不需要重复下载！";
			}
			Log.i(TAG, resultMsg);
		}
	}
}
