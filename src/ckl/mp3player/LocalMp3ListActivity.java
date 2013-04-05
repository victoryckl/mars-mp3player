package ckl.mp3player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ckl.model.Mp3Info;
import ckl.utils.FileUtils;

public class LocalMp3ListActivity extends ListActivity {
	private List<Mp3Info> mp3Infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateListView();
	}
	
	private void updateListView() {
		FileUtils fileUtils = new FileUtils();
		mp3Infos = fileUtils.getMp3Files("mp3/");
		SimpleAdapter adapter = buildSimpleAdapter(mp3Infos);
		setListAdapter(adapter);
	}
	
	private SimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3.name", mp3Info.getMp3Name());
			map.put("mp3.size", mp3Info.getMp3Size());
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.mp3info_item, 
				new String[]{"mp3.name", "mp3.size"}, new int[]{R.id.mp3_name, R.id.mp3_size});
		return simpleAdapter;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (mp3Infos != null) {
			Mp3Info info = mp3Infos.get(position);
			Intent intent = new Intent();
			intent.putExtra("mp3Info", info);
			intent.setClass(this, PlayerActivity.class);
			startActivity(intent);
		}
	}
}
