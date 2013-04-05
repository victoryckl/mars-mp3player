package ckl.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import ckl.model.Mp3Info;

public class FileUtils {
	private static final String TAG = "FileUtils";
	private String SDCardRoot;
	
	public String getSDPATH() {
		return SDCardRoot;
	}
	
	public FileUtils() {
		SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	
	/**
	 * 在SD卡上创建文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir) throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public File createSDDir(String dirName) throws IOException {
		File dir = new File(SDCardRoot + dirName + File.separator);
		if (!dir.mkdirs()) {
			Log.i(TAG, "mkdirs() falied! dir: " + dir.getAbsolutePath());
		}
		return dir;
	}
	
	/**
	 * 判断SD卡上文件是否存在
	 */
	public boolean isFileExist(String fileName){
		File file = new File(SDCardRoot + fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int readsize = 0;
			while((readsize = input.read(buffer)) > 0) {
				output.write(buffer, 0, readsize);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * 读取目录中的mp3文件的名字和大小
	 */
	public List<Mp3Info> getMp3Files(String path) {
		List<Mp3Info> infos = new ArrayList<Mp3Info>();
		
		File dir = new File(SDCardRoot + path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".mp3");
			}
		});
		
		if (files != null) {
			for (File f : files) {
				Mp3Info info = new Mp3Info();
				info.setMp3Name(f.getName());
				info.setMp3Size("" + f.length());
				infos.add(info);
			}
		}
		return infos;
	}
}
