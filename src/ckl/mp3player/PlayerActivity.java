package ckl.mp3player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.NClob;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import android.R.mipmap;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ckl.constant.Constant;
import ckl.lrc.LrcProcessor;
import ckl.lrc.LrcSentence;
import ckl.lrc.LrcView;
import ckl.model.Mp3Info;
import ckl.service.PlayerService;

public class PlayerActivity extends Activity {
	private static final String TAG = "PlayerActivity";
	private Mp3Info mp3Info;
	private Button mPlayBtn, mPauseBtn, mStopBtn;
	private LrcView mLrcView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		Intent intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

		init();
		play();
		
		mLrcUpdateReceiver = new LrcUpdateReceiver();
	}
	
	private void init() {
		mPlayBtn = (Button) findViewById(R.id.play);
		mPauseBtn = (Button) findViewById(R.id.pause);
		mStopBtn = (Button) findViewById(R.id.stop);

		mPlayBtn.setOnClickListener(mBtnClickListener);
		mPauseBtn.setOnClickListener(mBtnClickListener);
		mStopBtn.setOnClickListener(mBtnClickListener);

		mLrcView = (LrcView) findViewById(R.id.lrc_txt);
	}

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			Log.i(TAG, "onClick id = " + v.getId());
			switch (v.getId()) {
			case R.id.play:
				play();
				break;
			case R.id.pause:
				pause();
				break;
			case R.id.stop:
				stop();
				break;
			default:
				break;
			}
		}
	};
	
	private List<LrcSentence> mLrcList;
	private void play() {
		
		if (mp3Info.getLrcName() != null) {
			LrcProcessor lrcProcessor = new LrcProcessor();
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(Constant.SDCardRoot
						+ File.separator + "mp3" + File.separator + mp3Info.getLrcName());
				mLrcList = lrcProcessor.process_list(inputStream);
				mLrcView.setList(mLrcList);
				mLrcView.updateUI();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		Intent intent = new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.putExtra("MSG", Constant.PlayMsg.MSG_PLAY);
		intent.setClass(this, PlayerService.class);
		startService(intent);
	}

	private void pause() {
		Intent intent = new Intent();
		intent.putExtra("MSG", Constant.PlayMsg.MSG_PAUSE);
		intent.setClass(this, PlayerService.class);
		startService(intent);
	}

	private void stop() {
		Intent intent = new Intent();
		intent.putExtra("MSG", Constant.PlayMsg.MSG_STOP);
		intent.setClass(this, PlayerService.class);
		startService(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mLrcUpdateReceiver, getIntentFilter());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mLrcUpdateReceiver);
	}
	
	private LrcUpdateReceiver mLrcUpdateReceiver;
	class LrcUpdateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			mLrcView.setCurrentInfo(intent);
		}
	}
	
	private IntentFilter mIntentFilter;
	private IntentFilter getIntentFilter() {
		if (mIntentFilter == null) {
			mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(Constant.ACTION_LRC_UPDATE);
		}
		return mIntentFilter;
	}
}
