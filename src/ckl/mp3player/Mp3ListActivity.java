package ckl.mp3player;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ckl.constant.Constant;
import ckl.model.Mp3Info;
import ckl.utils.HttpDownloader;
import ckl.xml.Mp3ListContentHandler;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Mp3ListActivity extends ListActivity {
	private static final String TAG = "Mp3ListActivity";
	private ListView listView;

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
			String result = downloadXML(Constant.HOST_ADDRESS + "mp3/resources.xml");
			parse(result);
			break;
		case 101:
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
			for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
				Mp3Info mp3Info = (Mp3Info) iterator.next();
				Log.i(TAG, mp3Info.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mp3Infos;
	}
}
