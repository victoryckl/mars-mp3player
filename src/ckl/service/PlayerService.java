package ckl.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import ckl.lrc.LrcSentence;
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
		if (intent != null) {
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
//	private ArrayList<Queue> queues;
	private List<LrcSentence> mLrcList;
	private UpdateTimeCallback mUpdateTimeCallback;
	private void prepareLrc(String lrcName) {
		try {
			if (lrcName == null) {
				Log.i(TAG, "prepareLrc() lrcName is null !");
				return;
			}
			
			LrcProcessor lrcProcessor = new LrcProcessor();
			InputStream inputStream = new FileInputStream(Constant.SDCardRoot
					+ File.separator + "mp3" + File.separator + lrcName);
			
			mLrcList = lrcProcessor.process_list(inputStream);
			mUpdateTimeCallback = new UpdateTimeCallback(mLrcList);
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
		private List<LrcSentence> list;
		private int mLastIndex = -1;
		
		public UpdateTimeCallback(List<LrcSentence> list) {
			this.list = list;
		}
		
		@Override
		public void run() {
			if (mediaPlayer != null) {
				mCurrentMill = mediaPlayer.getCurrentPosition();
				int index = getCurrentIndex(mCurrentMill);
				if (mLastIndex != index) {
					mLastIndex = index;
					long playtime = getOneLrcPlayTime(index);
					
					Intent intent = new Intent();
					intent.setAction(Constant.ACTION_LRC_UPDATE);
					intent.putExtra("index", index);
					intent.putExtra("playtime", playtime);
					sendBroadcast(intent);
				}
				if (index < list.size()) {
					mHandler.postDelayed(mUpdateTimeCallback, 200);
				} else {
					mHandler.removeCallbacks(mUpdateTimeCallback);
					mLastIndex = -1;
				}
			}
		}
		
		private int getCurrentIndex(long time) {
			int index = -1;
			int size = list.size();
			long t = 0;
			
			for (int i = size - 1; i >= 0; i--) {
				t = list.get(i).getTime();
				time += 10;//提前变色
				if (time >= t) {
					index = i;
					break;
				}
			}
			
			return index;
		}
		
		private long getOneLrcPlayTime(int index) {
			long time = 0;
			long start = list.get(index).getTime();
			long end = 0;
			
			if (index + 1 >= list.size()) {
				end = mediaPlayer.getDuration();
			} else {
				end = list.get(index + 1).getTime(); 
			}
			
			time = end - start;
			
			return time;
		}
	}
}