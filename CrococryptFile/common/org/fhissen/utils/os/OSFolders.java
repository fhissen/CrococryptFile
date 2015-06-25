package org.fhissen.utils.os;

import java.io.File;

import org.fhissen.utils.os.OSDetector.OS;

public class OSFolders {
	public static final File getUserChooserstart(){
		String tmp;
		File ftmp;
		
		switch (OSDetector.which()) {
		case WIN:
			tmp = System.getenv("USERPROFILE");
			ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isDirectory()) return ftmp;

		case LIN:
		case MAC:
			tmp = System.getProperty("user.home");
			ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isDirectory()) return ftmp;
			
			if(OSDetector.which() == OS.WIN){
				tmp = System.getenv("SystemDrive");
				ftmp = new File(tmp);
				if(ftmp.exists() && ftmp.isDirectory()) return ftmp;
			}

		default:
			return new File("."); 
		}
	}
	
	public static final File getUserAppfolder(String keyword){
		File base = getUserBasefolder();
		if(!OSDetector.isWin()){
			keyword = "." + keyword.toLowerCase();
		}
		base = new File(base, keyword);
		return base;
	}
	
	public static final File makeUserAppfolder(String keyword){
		File base = getUserAppfolder(keyword);
		if(!base.exists()) base.mkdir();
		return base;
	}
	
	public static final File getUserBasefolder(){
		String tmp;
		File ftmp;
		
		switch (OSDetector.which()) {
		case WIN:
			tmp = System.getenv("appdata");
			ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isDirectory()) return ftmp;

		case LIN:
		case MAC:
			tmp = System.getProperty("user.home");
			ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isDirectory()) return ftmp;
		}
		
		return null;
	}

	public static final boolean isRoot(File f){
		if(f == null) return false;
		return isRoot(f.getAbsolutePath());
	}

	public static final boolean isRoot(String path){
		return isDriveRoot(path) || isNetworkRoot(path);
	}

	public static final boolean isNetworkRoot(File f){
		if(f == null) return false;
		return isNetworkRoot(f.getAbsolutePath());
	}

	public static final boolean isNetworkRoot(String path){
		if(path == null || path.length() == 0) return false;
		if(OSDetector.which() == OS.WIN){
			if(path.length() > 2 && path.startsWith("\\\\")){
				int i = path.indexOf('\\', 2);
				if(i < 0 || i== path.length() - 1) return true;
			}
		}
		return false;
	}

	public static final boolean isDriveRoot(File f){
		if(f == null) return false;
		return isDriveRoot(f.getAbsolutePath());
	}

	public static final boolean isDriveRoot(String path){
		if(path == null || path.length() == 0) return false;
		
		switch (OSDetector.which()) {
		case WIN:
			if(path.length() == 1 || path.length() > 3) return false;
			else if(path.length() == 2 && path.charAt(1) == ':') return true;
			else if(path.length() == 3 && path.charAt(1) == ':' && path.charAt(2) == '\\') return true;
			return false;

		case LIN:
		case MAC:
			if(path.length() > 1) return false;
			else if(path.length() == 1 && path.equals(File.separator)) return true;
			return false;
		}
		return false;
	}
	
	public static final String cutRoot(String abspath){
		if(abspath == null || abspath.length() == 0) return null;
		
		if(isRoot(abspath)){
			if(abspath.endsWith(File.separator)) return abspath;
			return abspath + File.separator;
		}

		switch (OSDetector.which()) {
		case LIN:
		case MAC:
			return File.separator;
			
		case WIN:
			if(abspath.startsWith("\\\\")){
				if(!abspath.endsWith(File.separator)) abspath += File.separator;
				return abspath.substring(0, abspath.indexOf('\\', 2) + 1);
			}
			else{
				return abspath.substring(0, 3);
			}
		}
		return null;
	}
}
