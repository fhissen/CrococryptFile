package org.fhissen.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.fhissen.utils.FileUtils;


public class KeyvalueFile {
	private File f;
	private HashMap<String, String> hm = new HashMap<>();
	
	public KeyvalueFile(File file){
		f = file;
	}
	
	public boolean exists(){
		return f != null && f.exists() && f.isFile() && f.length() > 1;
	}
	
	public String getPath(){
		if(f == null) return null;
		
		try {
			return f.getCanonicalPath();
		} catch (Exception e) {
			return f.getAbsolutePath();
		}
	}
	
	public String getDirectoryPath(){
		if(f == null) return null;
		
		try {
			return f.getParentFile().getCanonicalPath();
		} catch (Exception e) {
			return f.getParentFile().getAbsolutePath();
		}
	}
	
	public boolean empty(){
		return hm.size() == 0;
	}
	
	public void load(){
		if(!f.exists() || !f.isFile()) return;
		
		String raw = FileUtils.readFile(f.getAbsolutePath()).trim().replace("\r", "");
		String[] props = raw.split("\n");
		for(String s: props){
			if(s == null) continue;
			s = s.trim();
			if(s.length() <= 1) continue;
			
			int idx = s.indexOf("=");
			if(idx <= 0) continue;
			
			String key = s.substring(0, idx);
			String val = s.substring(idx + 1, s.length());
			hm.put(key.trim(), val.trim());
		}
	}
	
	public String get(String key){
		return hm.get(key);
	}
	
	public void set(String key, String val){
		hm.put(key, val);
	}
	
	public String get(IKeys key){
		return hm.get(key.name());
	}
	
	public int getInt(IKeys key){
		try {
			return Integer.parseInt(hm.get(key.name()));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public long getLong(IKeys key){
		try {
			return Long.parseLong(hm.get(key.name()));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void set(IKeys key, String val){
		hm.put(key.name(), val);
	}

	public void set(IKeys key, Object val){
		hm.put(key.name(), val.toString());
	}
	
	public void setB64(IKeys key, byte[] b){
		set(key, Base64.encodeBase64URLSafeString(b));
	}
	
	public byte[] getB64(IKeys key){
		return Base64.decodeBase64(hm.get(key.name()));
	}

	public void save(){
		if(f == null) return;
		
		StringBuilder sb = new StringBuilder();
		Iterator<String> keys = hm.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			sb.append(key);
			sb.append('=');
			sb.append(hm.get(key));
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
