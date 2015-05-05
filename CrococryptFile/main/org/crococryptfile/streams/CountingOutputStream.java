package org.crococryptfile.streams;
import java.io.IOException;
import java.io.OutputStream;


public class CountingOutputStream extends OutputStream {
	private long no = 0;
	private long len = 0;
	private OutputStream os;
	
	public CountingOutputStream(OutputStream os){
		this.os = os;
	}
	
	public void setStartNo(long n){
		no = n;
	}

	@Override
	public void write(int b) throws IOException {
		no++;
		len++;
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		no += len - off;
		this.len += len - off;
		os.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		no+=b.length;
		len+=b.length;
		os.write(b);
	}

	
	public void realClose() throws IOException{
		os.close();
	}
	
	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}
	
	public void newItem(){
		len = 0;
	}

	public long getNumber(){
		return no;
	}

	public long getLength(){
		return len;
	}
}
