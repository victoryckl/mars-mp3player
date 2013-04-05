package ckl.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ckl.constant.Constant;
import ckl.model.Mp3Info;
import ckl.service.PlayerService;

public class PlayerActivity extends Activity {
	private static final String TAG = "PlayerActivity";
	private Mp3Info mp3Info;
	private Button mPlayBtn, mPauseBtn, mStopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		Intent intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

		init();
		play();
	}

	private void init() {
		mPlayBtn = (Button) findViewById(R.id.play);
		mPauseBtn = (Button) findViewById(R.id.pause);
		mStopBtn = (Button) findViewById(R.id.stop);

		mPlayBtn.setOnClickListener(mBtnClickListener);
		mPauseBtn.setOnClickListener(mBtnClickListener);
		mStopBtn.setOnClickListener(mBtnClickListener);
	}

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick id = " + v.getId());
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

	private void play() {
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
}
