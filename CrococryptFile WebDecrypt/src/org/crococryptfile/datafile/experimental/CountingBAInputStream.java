package org.crococryptfile.datafile.experimental;

import java.io.IOException;
import java.io.InputStream;

import org.fhissen.utils.ByteUtils;

public class CountingBAInputStream extends InputStream {
	private byte[] buf;
	private int len;
	private int pos = 0;
	private boolean executeClose = false;

	public CountingBAInputStream(byte[] buf, boolean executeclose){
		this(buf);
		this.executeClose = executeclose;
	}

	public CountingBAInputStream(byte[] buf){
		this.buf = buf;
		len = buf.length;
	}
	
	public CountingBAInputStream clone(){
		return new CountingBAInputStream(buf);
	}
	
	public int dumpLength(){
		return buf.length;
	}
	
	public InputStream getIS(){
		CountingBAInputStream my = new CountingBAInputStream(buf);
		my.pos = this.pos;
		my.len = this.len;
		return my;
	}
	
	public void newItem(long n, long l){
		len = (int) l;
		
		try {
			if(n < 0 || n >= buf.length) return;
			pos = (int) n;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int available() throws IOException {
		return (int) len;
	}

	public void realClose() throws IOException{
		buf = null;
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

		int i = ByteUtils.b2i(buf[pos]);
		pos++;
		return i;
	}
	
	@Override
	public int read(byte[] b, int off, int rlen) throws IOException {
		if(len <= 0) return -1;
		
		if(rlen > len) rlen = (int) len;
		int readsize = rlen;
		
		System.arraycopy(buf, pos, b, off, rlen);
		pos += rlen;
		len -= readsize;
		
		return readsize;
	}
}
