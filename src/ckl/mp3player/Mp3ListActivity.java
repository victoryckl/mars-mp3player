package ckl.mp3player;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class Mp3ListActivity extends ListActivity {
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp3_list);
		
		
	}

	private void init() {
		listView = (ListView)findViewById(android.R.id.list);
		setListEmptyView();
	}
	
	private void setListEmptyView() {
		View emptyView = findViewById(R.id.empty);
		listView.setEmptyView(emptyView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mp3_list, menu);
		return true;
	}

}
