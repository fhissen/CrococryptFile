package org.fhissen.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.fhissen.crypto.CryptoUtils;


public class FileWipe {
	private static final int BUFFERSIZE = 1024 * 1024;

	public static File wipe(File f){
		if(!f.exists() || !f.isFile()) {
			return null;
		}
		
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			
			byte[] b = new byte[BUFFERSIZE];
			
			long l = raf.length();
			while(l > 0){
				CryptoUtils.kill(b);
				
				if(l < BUFFERSIZE){
					raf.write(b, 0, (int)l);
					l = 0;
				}
				else{
					raf.write(b);
					l -= BUFFERSIZE;
				}
			}
			
			Thread.sleep(500);
			
			raf.setLength(0);
			raf.close();
			
			String name = f.getName();
			char[] a = name.toCharArray();
			Arrays.fill(a, 'a');
			
			File newname = new File(f.getParentFile(), new String(a));
			if(!f.renameTo(newname)) return f;
			
			f = new File(f.getParentFile(), "a");
			if(!newname.renameTo(f)) return newname;
			
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
