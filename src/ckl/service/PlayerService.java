package ckl.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import ckl.constant.Constant;
import ckl.constant.Constant.PlayState;
import ckl.lrc.LrcProcessor;
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
					prepareLrc(mp3Info.getLrcName());
					Uri uri = Uri.parse("file://" + getMp3Path(mp3Info));
					Log.i(TAG, "play uri : " + uri);
					mediaPlayer = MediaPlayer.create(this, uri);
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer arg0) {
							stop();
						}
					});
					mHandler.postDelayed(mUpdateTimeCallback, 5);
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
				mHandler.postDelayed(mUpdateTimeCallback, 5);
			}
			break;
		}
	}

	private void pause() {
		if (mediaPlayer != null) {
			if (mPlayState == PlayState.MPS_PLAYING) {
				setPlayState(PlayState.MPS_PAUSE);
				mediaPlayer.pause();
				mHandler.removeCallbacks(mUpdateTimeCallback);
			}
		}
	}

	private void stop() {
		setPlayState(PlayState.MPS_PREPARE);
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			mHandler.removeCallbacks(mUpdateTimeCallback);
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
	
	//------------------------
	private Handler mHandler = new Handler();
	private long mCurrentMill, mNextMill;
	private ArrayList<Queue> queues;
	private UpdateTimeCallback mUpdateTimeCallback;
	private void prepareLrc(String lrcName) {
		try {
			InputStream inputStream = new FileInputStream(Constant.SDCardRoot
					+ File.separator + "mp3" + File.separator + lrcName);
			LrcProcessor lrcProcessor = new LrcProcessor();
			queues = lrcProcessor.process(inputStream);
			mUpdateTimeCallback = new UpdateTimeCallback(queues);
			mCurrentMill = 0;
			mNextMill = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class UpdateTimeCallback implements Runnable {
		private Queue<Long> times;
		private Queue<String> messages;
		private String message;
		public UpdateTimeCallback(ArrayList<Queue> queues) {
			times = queues.get(0);
			messages = queues.get(1);
			mNextMill = (Long)times.poll();
			message = (String)messages.poll();
		}
		@Override
		public void run() {
			if (mediaPlayer != null) {
				Log.i(TAG, "" + mediaPlayer.getCurrentPosition() + "/" + mediaPlayer.getDuration());
				mCurrentMill = mediaPlayer.getCurrentPosition();
				Log.i(TAG, "mNextMill: " + mNextMill);
				if (mCurrentMill >= mNextMill) {
					Log.i(TAG, "mLrcView message = " + message);
					if (message != null) {
						Intent intent = new Intent();
						intent.setAction(Constant.ACTION_LRC_UPDATE);
						intent.putExtra("lrc_text", message);
						sendBroadcast(intent);
					}
					mNextMill = (Long)times.poll();
					message = (String)messages.poll();
				}
				mHandler.postDelayed(mUpdateTimeCallback, 200);
			}

		}
	}
}