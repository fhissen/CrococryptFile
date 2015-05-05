package org.crococryptfile.datafile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.fhissen.utils.Codes;
import org.fhissen.utils.FileWipe;
import org.fhissen.utils.StreamCopy;


public class Index {
	private OutputStream idx;
	private File cachefile;
	private long count = 0;
	
	public Index(File cachefile){
		count = 0;
		this.cachefile = cachefile;
		cachefile.deleteOnExit();
		try {
			idx = new FileOutputStream(cachefile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void idxEntry(String name, long no, long len, long last, byte[] iv) throws IOException{
		IndexEntry entry = new IndexEntry();
		
		entry.ATTRIBUTE_NAME = name.getBytes(Codes.UTF8);
		entry.ATTRIBUTE_NAMELEN = entry.ATTRIBUTE_NAME.length;
		entry.ATTRIBUTE_IV = iv;
		entry.ATTRIBUTE_MODIFIED = last;
		entry.ATTRIBUTE_SIZE = len;
		entry.ATTRIBUTE_OFFSET = no;

		entry.write(idx);
		
		count++;
	}
	
	public long getCount(){
		return count;
	}
	
	
	public void write(OutputStream out) throws IOException{
		close();
		new StreamCopy().copyNclose(new FileInputStream(cachefile), out);
		delete();
	}
	
	public void delete() throws IOException{
		close();
		
		File f = FileWipe.wipe(cachefile);
		
		if(f != null){
			f.delete();
		}
		else {
			cachefile.delete();
		}
	}
	
	public void close() throws IOException{
		idx.flush();
		idx.close();
		count = 0;
	}
}
