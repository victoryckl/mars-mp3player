package ckl.mp3player;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ckl.constant.Constant;
import ckl.model.Mp3Info;
import ckl.service.DownloadService;
import ckl.utils.HttpDownloader;
import ckl.xml.Mp3ListContentHandler;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Mp3ListActivity extends ListActivity {
	private static final String TAG = "Mp3ListActivity";
	private ListView listView;
	private List<Mp3Info> mp3Infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp3_list);
		
		init();
	}

	private void init() {
		HttpDownloader.initStrictMode();
		
		listView = (ListView)findViewById(android.R.id.list);
		setListEmptyView();
		updateListView();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getOrder()) {
		case 100:
			updateListView();
			break;
		case 101:
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateListView() {
		String result = downloadXML(Constant.HOST_ADDRESS + "mp3/resources.xml");
		mp3Infos = parse(result);
		SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3Infos);
		listView.setAdapter(simpleAdapter);
	}
	
	private SimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info)iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3.name", mp3Info.getMp3Name());
			map.put("mp3.size", mp3Info.getMp3Size());
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.mp3info_item, 
				new String[]{"mp3.name", "mp3.size"}, new int[]{R.id.mp3_name, R.id.mp3_size});
		return simpleAdapter;
	}
	
	private String downloadXML(String urlStr) {
		HttpDownloader httpDownloader = new HttpDownloader();
		String result = httpDownloader.download(urlStr);
		return result;
	}
	
	private List<Mp3Info> parse(String xmlStr) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(mp3Infos);
			xmlReader.setContentHandler(mp3ListContentHandler);
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
//			for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
//				Mp3Info mp3Info = (Mp3Info) iterator.next();
//				Log.i(TAG, mp3Info.toString());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mp3Infos;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Mp3Info mp3Info = mp3Infos.get(position);
//		Log.i(TAG, "onListItemClick() mp3Info = " + mp3Info);
		startDownloadService(mp3Info);
		super.onListItemClick(l, v, position, id);
	}
	
	private void startDownloadService(Mp3Info mp3Info) {
		Intent intent  = new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.setClass(this, DownloadService.class);
		startService(intent);
	}
}
