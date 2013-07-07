package ckl.lrc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class LrcProcessor {
	private static final String TAG = "LrcProcessor";

	public ArrayList<Queue> process(InputStream inputStream) {
		ArrayList<Queue> queues = new ArrayList<Queue>();
		Queue<Long> timeMills = new LinkedList<Long>();
		Queue<String> messages = new LinkedList<String>();
		try {
			InputStreamReader inputReader = new InputStreamReader(inputStream, "GBK");
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			
			String line = null;
//			Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
			Pattern p = Pattern.compile("^\\[\\d\\d:\\d\\d\\.\\d\\d\\]");//^\[\d\d:\d\d\.\d\d\]
			String result = null;
			while((line = bufferedReader.readLine()) != null) {
//				Log.i(TAG, "line = " + line);
				if (line.length() > 0) {
					Matcher m = p.matcher(line);
					if (m.find()) {
						if (result != null){
//							Log.i(TAG, "result --> " + result);
							messages.add(result);
							result = null;
						}
						String timeStr = m.group();
						Long timeMill = time2Long(timeStr.substring(1, timeStr.length() -1));
						timeMills.offer(timeMill);
						String msg = line.substring(10);
						if (msg != null) {
							result = "" + msg + "\n";
						}
					} else {
						if (result != null) {
							result = result + line + "\n";
						}
					}
				}
			}
			messages.add(result);
			queues.add(timeMills);
			queues.add(messages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queues;
	}
	
	public List<LrcSentence> process_list(InputStream inputStream) {
		List<LrcSentence> list = new ArrayList<LrcSentence>();
		Queue<Long> timeMills = new LinkedList<Long>();
		Queue<String> messages = new LinkedList<String>();
		try {
			InputStreamReader inputReader = new InputStreamReader(inputStream, "GBK");
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			
			String line = null;
			String lrc = null;
			long time = 0;
			LrcSentence sentence = null;
			
//			Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
			Pattern p = Pattern.compile("^\\[\\d\\d:\\d\\d\\.\\d\\d\\]");//^\[\d\d:\d\d\.\d\d\]
			
			while((line = bufferedReader.readLine()) != null) {
//				Log.i(TAG, "line = " + line);
				if (line.length() > 0) {
					Matcher m = p.matcher(line);
					if (m.find()) {
						if (lrc != null){
//							Log.i(TAG, "lrc --> " + lrc);
							sentence.setLrc(lrc);
							lrc = null;
							list.add(sentence);
						}
						String timeStr = m.group();
						time = time2Long(timeStr.substring(1, timeStr.length() -1));
						
						sentence = new LrcSentence(time);
						String msg = line.substring(10);
						if (msg != null) {
							lrc = "" + msg + "\n";
						}
					} else {
						if (lrc != null) {
							lrc = lrc + line + "\n";
						}
					}
				}
			}
			sentence = new LrcSentence(lrc, time);
			list.add(sentence);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sort(list);
		
		printLrcList(list);
		return list;
	}
	
	public static void printLrcList(List<LrcSentence> list) {
		if (list == null) {
			Log.i(TAG, "printLrcList() list is null");
			return ;
		}
		
		int size = list.size();
		if (size <= 0) {
			Log.i(TAG, "printLrcList() list size == 0");
			return ;
		}
		
		for(int i = 0; i < size; i++) {
			Log.i(TAG, list.get(i).toString());
		}
	}
	
	// sort by time
	private List<LrcSentence> sort(List<LrcSentence> list) {
		//TODO:
		return list;
	}
	
	private Long time2Long(String timeStr) {
		String s[] = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String ss[] = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		long l = min * 60 * 1000 + sec * 1000 + mill * 10L;
//		Log.i(TAG, "timeStr --> " + timeStr);
		return l;
	}
}
