package org.crococryptfile.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.fhissen.crypto.CryptoCodes;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.StreamMachine;
import org.fhissen.utils.StreamMachine.StreamResult;


public class IndexEntry {
	public int ATTRIBUTE_NAMELEN;
	public byte[] ATTRIBUTE_NAME;
	public byte[] ATTRIBUTE_IV;
	public long ATTRIBUTE_MODIFIED;
	public long ATTRIBUTE_CREATED;
	public long ATTRIBUTE_SIZE;
	public long ATTRIBUTE_OFFSET;
	public long ATTRIBUTE_FSATTRIBUTES;
	public long ATTRIBUTE_OTHER;
	
	public void write(OutputStream os) throws IOException{
		if(ATTRIBUTE_NAME == null) throw new IOException("file name failure, no null filename allowed");
		if(ATTRIBUTE_IV == null || ATTRIBUTE_IV.length != CryptoCodes.STANDARD_IVSIZE) throw new IOException("IV has wrong size!");
		
		StreamMachine.write(os,
				ATTRIBUTE_NAMELEN,
				ATTRIBUTE_NAME,
				ATTRIBUTE_IV,
				ATTRIBUTE_MODIFIED,
				ATTRIBUTE_CREATED,
				ATTRIBUTE_SIZE,
				ATTRIBUTE_OFFSET,
				ATTRIBUTE_FSATTRIBUTES,
				ATTRIBUTE_OTHER
				);
	}
	
	public static final IndexEntry readFrom(InputStream is) throws IOException{
		IndexEntry entry = new IndexEntry();
		
		byte[] buffer_head = new byte[4]; 
		StreamResult se = StreamMachine.read(is, buffer_head);
		if(se == StreamResult.EndOfStream) return null;
		else if(se == StreamResult.ReadException) throw new IOException();
		
		try {
			entry.ATTRIBUTE_NAMELEN = ByteUtils.bytesToInt(buffer_head);
			entry.ATTRIBUTE_NAME = StreamMachine.read(is, entry.ATTRIBUTE_NAMELEN);
			entry.ATTRIBUTE_IV = StreamMachine.read(is, CryptoCodes.STANDARD_IVSIZE);
			entry.ATTRIBUTE_MODIFIED = StreamMachine.readO(is, entry.ATTRIBUTE_MODIFIED);
			entry.ATTRIBUTE_CREATED = StreamMachine.readO(is, entry.ATTRIBUTE_CREATED);
			entry.ATTRIBUTE_SIZE = StreamMachine.readO(is, entry.ATTRIBUTE_SIZE);
			entry.ATTRIBUTE_OFFSET = StreamMachine.readO(is, entry.ATTRIBUTE_OFFSET);
			entry.ATTRIBUTE_FSATTRIBUTES = StreamMachine.readO(is, entry.ATTRIBUTE_FSATTRIBUTES);
			entry.ATTRIBUTE_OTHER = StreamMachine.readO(is, entry.ATTRIBUTE_OTHER);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException();
		}
		
		return entry;
	}
}
