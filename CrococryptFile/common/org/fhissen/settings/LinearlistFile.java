package org.fhissen.settings;

import java.io.File;
import java.util.ArrayList;

import org.fhissen.utils.FileUtils;

public class LinearlistFile {
	private File f;
	private ArrayList<String> arr = new ArrayList<>();
	
	public LinearlistFile(File file){
		f = file;
	}
	
	
	public String getPath(){
		if(f == null) return null;
		
		return f.getAbsolutePath();
	}
	
	public boolean empty(){
		return arr.size() == 0;
	}
	
	public void load(){
		if(!f.exists() || !f.isFile()) return;
		
		String raw = FileUtils.readFile(f.getAbsolutePath()).trim().replace("\r", "");
		String[] props = raw.split("\n");
		for(String s: props){
			if(s == null || s.length() == 0) continue;
			arr.add(s);
		}
	}
	
	public String get(int key){
		return arr.get(key);
	}
	
	public void add(String val){
		if(val == null || val.length() == 0 || contains(val)) return;
		
		arr.add(val);
	}
	
	public boolean remove(String val){
		return arr.remove(val);
	}
	
	public boolean contains(String val){
		return arr.contains(val);
	}
	
	public int length(){
		return arr.size();
	}
	
	public void save(){
		if(f == null) return;
		
		StringBuilder sb = new StringBuilder();
		for(String s: arr){
			sb.append(s);
			sb.append('\r');
			sb.append('\n');
		}
		
		try {
			File tmp = new File(f.getParentFile(), ".tmp");
			FileUtils.writeFileUTF8(sb.toString(), tmp.getAbsolutePath());
			f.delete();
			tmp.renameTo(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
