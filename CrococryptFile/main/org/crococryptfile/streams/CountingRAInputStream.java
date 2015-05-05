package org.crococryptfile.streams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


public class CountingRAInputStream extends InputStream{
	private long len;
	private RandomAccessFile raf;
	private boolean executeClose = false;

	public CountingRAInputStream(File f, boolean executeClose){
		this(f);
		this.executeClose = executeClose;
	}

	public CountingRAInputStream(File f){
		try {
			raf = new RandomAccessFile(f, "r");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void newItem(long n, long l){
		len = l;
		
		try {
			if(n < 0 || n >= raf.length()) return;
			raf.seek(n);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int available() throws IOException {
		return (int) len;
	}

	public void realClose() throws IOException{
		raf.close();
	}
	
	@Override
	public void close() throws IOException {
		if(executeClose) realClose();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read() throws IOException {
		if(len <= 0) return -1;
		len--;

		return raf.read();
	}
	
	@Override
	public int read(byte[] b, int off, int rlen) throws IOException {
		if(len <= 0) return -1;
		
		
		if(rlen > len) rlen = (int) len;
		int readsize = raf.read(b, off, rlen);
		len -= readsize;
		return readsize;
	}
}
