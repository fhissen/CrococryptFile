package org.crococryptfile.datafile.experimental;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ItemZipper {
	private ZipOutputStream out;
	
	public ItemZipper(OutputStream out){
		this.out = new ZipOutputStream(out);
	}
	
	public void close() throws IOException{
		out.flush();
		out.close();
	}
	
	public OutputStream newItem(String namepath, long modified, long size) throws IOException{
		ZipEntry entry = new ZipEntry(saniZipPath(namepath));
		entry.setTime(modified);
		entry.setSize(size);
		out.putNextEntry(entry);
		return out;
	}
	
	public void start(){
	}
	
	public void closeLast(){
	}
	
	public void cancelLast(){
	}
	
	private final static String saniZipPath(String tmp){
		return tmp.replace('\\', '/');
	}
}
