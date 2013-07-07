package ckl.mp3player;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Mp3Player extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources();

		TabHost tabHost = getTabHost();
		
		Intent localIntent = new Intent();
		localIntent.setClass(this, LocalMp3ListActivity.class);
		
		TabHost.TabSpec localSpec = tabHost.newTabSpec("Local");
		localSpec.setIndicator("Local", 
				res.getDrawable(android.R.drawable.stat_sys_upload));
		
		localSpec.setContent(localIntent);
		tabHost.addTab(localSpec);
		
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(this, Mp3ListActivity.class);
		
		TabHost.TabSpec remoteSpec = tabHost.newTabSpec("Remote");
		remoteSpec.setIndicator("Remote",
				res.getDrawable(android.R.drawable.stat_sys_download));
		
		remoteSpec.setContent(remoteIntent);
		tabHost.addTab(remoteSpec);
	}
}
