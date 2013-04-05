package ckl.service;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import ckl.constant.Constant;
import ckl.constant.Constant.PlayState;
import ckl.model.Mp3Info;

public class PlayerService extends Service {
	private static final String TAG = "PlayerService";
	private int mPlayState = PlayState.MPS_PREPARE; // 当前播放状态
	private MediaPlayer mediaPlayer;
	private Mp3Info lastMp3Info;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Mp3Info mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		int msgId = intent.getIntExtra("MSG", Constant.PlayMsg.MSG_STOP);
		Log.i(TAG, "msgId = " + msgId);
		switch (msgId) {
		case Constant.PlayMsg.MSG_PLAY:
			play(mp3Info);
			break;
		case Constant.PlayMsg.MSG_PAUSE:
			pause();
			break;
		case Constant.PlayMsg.MSG_STOP:
			stop();
			break;

		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void play(Mp3Info mp3Info) {
		Log.i(TAG, "" + lastMp3Info);
		if (lastMp3Info != null) {
			if (!lastMp3Info.isSameMp3(mp3Info)) {
				stop();
				lastMp3Info = mp3Info;
			}
		} else {
			lastMp3Info = mp3Info;
		}
		
		switch(mPlayState)
		{
		case PlayState.MPS_PREPARE:
			if (mp3Info != null) {
				setPlayState(PlayState.MPS_PLAYING);
				try {
					Uri uri = Uri.parse("file://" + getMp3Path(mp3Info));
					Log.i(TAG, "play uri : " + uri);
					mediaPlayer = MediaPlayer.create(this, uri);
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case PlayState.MPS_PAUSE:
			setPlayState(PlayState.MPS_PLAYING);
			if (mediaPlayer != null) {
				mediaPlayer.start();
			}
			break;
		}
		

	}

	private void pause() {
		if (mediaPlayer != null) {
			if (mPlayState == PlayState.MPS_PLAYING) {
				setPlayState(PlayState.MPS_PAUSE);
				mediaPlayer.pause();
			}
		}
	}

	private void stop() {
		if (mediaPlayer != null) {
			setPlayState(PlayState.MPS_PREPARE);
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	private synchronized void setPlayState(int state) {
		mPlayState = state;
	}
	
	private String getMp3Path(Mp3Info mp3Info) {
		String path = Constant.SDCardRoot + File.separator + "mp3"
				+ File.separator + mp3Info.getMp3Name();
		return path;
	}
}
