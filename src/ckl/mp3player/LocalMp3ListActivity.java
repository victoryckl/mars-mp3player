package ckl.mp3player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import ckl.model.Mp3Info;
import ckl.utils.FileUtils;

public class LocalMp3ListActivity extends ListActivity {
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
		List<Mp3Info> mp3Infos = fileUtils.getMp3Files("mp3/");
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3_name", mp3Info.getMp3Name());
			map.put("mp3_size", mp3Info.getMp3Size());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, list, 
				R.layout.mp3info_item, 
				new String[]{"mp3_name", "mp3_size"}, 
				new int[]{R.id.mp3_name, R.id.mp3_size});
		setListAdapter(adapter);
	}
}
