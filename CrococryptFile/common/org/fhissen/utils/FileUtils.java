package org.fhissen.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.fhissen.utils.os.OSFolders;

public class FileUtils {
	private final byte buffer[] = new byte[1024 * 1024];

	public static class ThreadRunner{
		private boolean run = true;
		
		public void stop(){
			run = false;
		}
	}
	
	public void copy(InputStream f_in, String dest, ThreadRunner tr) {
		FileOutputStream f_out = null;

		try {
			f_out = new FileOutputStream(dest);
			int n_bytes;
			try {
				while (tr.run) {
					n_bytes = f_in.read(buffer);
					if (n_bytes <= -1)
						break;
					f_out.write(buffer, 0, n_bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			f_out.flush();
		} catch (Exception e) {
			System.out.println("error reading or writing");
			e.printStackTrace();
		} finally {
			if (f_in != null)
				try {
					f_in.close();
				} catch (IOException e) {
					System.out.println("close1 failed!");
				}
			if (f_out != null)
				try {
					f_out.close();
				} catch (IOException e) {
					System.out.println("close2 failed!");
				}
		}
	}

	public static String readFile(String file){
		return readFile(new File(file));
	}

	public static String readFile(File file){
		FileInputStream fis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		try {
			fis = new FileInputStream(file);
			
			byte[] thearray = new byte[1024 * 16];
			int b = 0;
			
			while (true){
				try {
					b = fis.read(thearray);
					if (b>=0){
						baos.write(thearray, 0, b);
					}
					else{
						fis.close();
						break;
					}
				} catch (Exception e) {
					break;
				}			
			}

		} catch (IOException e) {
			return null;
		} finally{
			try {
				if(fis!=null)fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			return new String(baos.toString(Codes.UTF8));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void writeFile(String string, String file){
		try {
			File outFile = new File(file);
			FileWriter out = new FileWriter(outFile);
			out.write(string);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void writeFileUTF8(String string, String file){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(string.getBytes(Codes.UTF8));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final void writeBytesToFileend(byte[] bytes, File f) {
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.seek(f.length());
			raf.write(bytes);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final void readBytesFromFileend(File f, byte[] b){
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			raf.seek(f.length() - b.length);
			raf.readFully(b);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final void writeBytesToFilestart(byte[] bytes, File f) {
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.write(bytes);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final void readBytesFromFilestart(File f, byte[] b){
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			raf.readFully(b);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String shortenDisplayFile(File f){
		StringBuilder sb = new StringBuilder();
		String path = f.getAbsolutePath();
		
		if(OSFolders.isRoot(path))
			return path;
		
		sb.append(OSFolders.cutRoot(path));
		
		File f_parent = f.getParentFile();
		if(OSFolders.isRoot(f_parent)){
			sb.append(f.getName());
			return sb.toString();
		}
		
		if(!OSFolders.isRoot(f_parent.getParentFile())){
			sb.append("...");
			sb.append(File.separator);
		}

		sb.append(f_parent.getName());
		sb.append(File.separator);
		sb.append(f.getName());

		return sb.toString();
	}

    public static final void deltree(Path thedir){
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(thedir)) {
            for (Path path : directoryStream) {
            	if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) deltree(path);
            	else Files.delete(path);
            }
            
            Files.delete(thedir);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }
    
	public static final int noOfFiles(Path thedir){
		int ret = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(thedir)) {
            for (@SuppressWarnings("unused") Path path : directoryStream) {
            	ret++;
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return ret;
	}
}
