package org.fhissen.utils.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;


public class SwingHacks_FileSystemView extends FileSystemView{
	private static final File fileit(File f, String sub){
		if(f == null && sub != null) return new File(sub); 
		
		if(f != null && sub != null) return new File(f, sub);

		if(f != null && sub == null) return new File(f.getAbsolutePath());

		return new File(".");
	}

	private FileSystemView view = FileSystemView.getFileSystemView();
	
	private ArrayList<String> roots_s = new ArrayList<>();
	private ArrayList<File> roots_f = new ArrayList<>();
	
	{
		File[] roots = File.listRoots();
		Collections.addAll(roots_f, roots);
		for(File f: roots)
			roots_s.add(f.getAbsolutePath());
	}
	
	@Override
	public File createFileObject(File arg0, String arg1) {
		File tmp = null;
		try {
			tmp = view.createFileObject(arg0, arg1);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		try {
			tmp = super.createFileObject(arg0, arg1);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		return fileit(arg0, arg1);
	}

	@Override
	public File createFileObject(String arg0) {
		File tmp = null;
		try {
			tmp = view.createFileObject(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.createFileObject(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return fileit(null, arg0);
	}

	@Override
	protected File createFileSystemRoot(File arg0) {
		try {
			String tmp = arg0.getAbsolutePath();
			if(tmp == null || tmp.length() == 0 || tmp.startsWith(".") || tmp.startsWith("\\"))
				return new File("C:\\");
			return new File(tmp.charAt(0)+":\\");
		} catch (Exception e) {
		}
		return new File("C:\\");
	}

	public File createNewFolder(File arg0) throws IOException {
		File tmp = null;
		try {
			tmp = view.createNewFolder(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return fileit(arg0, "NewFolder");
	}

	public File getChild(File arg0, String arg1) {
		File tmp = null;
		try {
			tmp = view.getChild(arg0, arg1);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.getChild(arg0, arg1);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return createFileObject(arg0, arg1);
	}

	public File getDefaultDirectory() {
		File tmp = null;
		try {
			tmp = view.getDefaultDirectory();
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = view.getDefaultDirectory();
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return new File(System.getProperty("user.home"));
	}

	public File[] getFiles(File arg0, boolean arg1) {
		File[] tmp = null;
		try {
			tmp = view.getFiles(arg0, arg1);
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.getFiles(arg0, arg1);
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		} catch (Exception e) {
		}
		
		if(arg0 != null){
			tmp = arg0.listFiles();
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		}
		return new File[]{new File("C:\\")};
	}

	public File getHomeDirectory() {
		File tmp = null;
		try {
			tmp = view.getHomeDirectory();
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.getHomeDirectory();
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return new File(System.getProperty("user.home"));
	}

	public File getParentDirectory(File arg0) {
		File tmp = null;
		try {
			tmp = view.getParentDirectory(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		try {
			tmp = super.getParentDirectory(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		try {
			if(arg0 != null) tmp = arg0.getParentFile();
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		return new File("C:\\");
	}

	public File[] getRoots() {
		File[] tmp = null;
		try {
			tmp = File.listRoots();
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = view.getRoots();
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.getRoots();
			if(tmp != null && (tmp.length > 0 && tmp[0] != null || tmp.length == 0)) return tmp;
		} catch (Exception e) {
		}
		return new File[]{new File("C:\\")};
	}

	public String getSystemDisplayName(File arg0) {
		if(arg0 != null){
			if(isRoot(arg0)) return arg0.getAbsolutePath();
		}
		
		String tmp = null;
		try {
			tmp = view.getSystemDisplayName(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}

		try {
			tmp = super.getSystemDisplayName(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}

		try {
			return arg0.getName();
		} catch (Exception e) {
		}
		
		return "unknown";
	}

	public Icon getSystemIcon(File arg0) {
		Icon tmp = null;

		try {
			tmp = super.getSystemIcon(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		
		try {
			tmp = view.getSystemIcon(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}

		return null;
	}

	public String getSystemTypeDescription(File arg0) {
		String tmp = null;
		try {
			tmp = view.getSystemTypeDescription(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		try {
			tmp = super.getSystemTypeDescription(arg0);
			if(tmp != null) return tmp;
		} catch (Exception e) {
		}
		return "Unknown";
	}

	public boolean isComputerNode(File arg0) {
		try {
			return view.isComputerNode(arg0);
		} catch (Exception e) {
		}
		try {
			return super.isComputerNode(arg0);
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isDrive(File arg0) {
		if(arg0 != null){
			String tmp = arg0.getAbsolutePath();
			if(tmp.endsWith(":\\") || tmp.endsWith(":")) return true;
		}

		try {
			return view.isDrive(arg0);
		} catch (Exception e) {
		}
		try {
			return super.isDrive(arg0);
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isFileSystem(File arg0) {
		try {
			return view.isFileSystem(arg0);
		} catch (Exception e) {
		}
		try {
			return super.isFileSystem(arg0);
		} catch (Exception e) {
		}
		return true;
	}

	public boolean isFileSystemRoot(File arg0) {
		return isDrive(arg0);
	}

	public boolean isFloppyDrive(File arg0) {
		try {
			return view.isFloppyDrive(arg0);
		} catch (Exception e) {
		}
		try {
			return super.isFloppyDrive(arg0);
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isHiddenFile(File arg0) {
		if(arg0 != null) return arg0.isHidden();
		return false;
	}

	public boolean isParent(File arg0, File arg1) {
		if(arg0 != null && arg1 != null){
		try {
			arg1.getParentFile().equals(arg0);
		} catch (Exception e) {
		}
	}
		

		return false;
	}

	public boolean isRoot(File arg0) {
		if(arg0 != null){
			if(roots_f.contains(arg0)) return true;
			if(roots_s.contains(arg0.getAbsolutePath())) return true;
		}


		return false;
	}

	public Boolean isTraversable(File arg0) {
		try {
			return view.isTraversable(arg0);
		} catch (Exception e) {
		}
		try {
			return super.isTraversable(arg0);
		} catch (Exception e) {
		}
		return arg0.isDirectory();
	}
}
