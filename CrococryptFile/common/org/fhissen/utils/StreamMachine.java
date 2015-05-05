package org.fhissen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamMachine {
	public enum StreamResult{
		OK,
		EndOfStream,
		ReadException,
	}
	
	private InputStream is;
	
	public StreamMachine(InputStream is){
		this.is = is;
	}
	
	public byte[] read(int numofbytes){
		return read(is, numofbytes);
	}
	
	public StreamResult read(byte[] buffer){
		return read(is, buffer);
	}
	
	public <T>T readO(T o){
		return readO(is, o);
	}
	
	public static final void write(OutputStream os, Object... arr) throws IOException{
		for(Object o: arr){
			if(o instanceof byte[]){
				os.write((byte[]) o);
			}
			else{
				os.write(ByteUtils.objToBytes(o));
			}
		}
	}

	
	public static final byte[] read(InputStream in, int numofbytes){
		byte[] ret = new byte[numofbytes];
		read(in, ret);
		return ret;
	}
	
	public static final StreamResult read(InputStream in, byte[] buffer){
		if(buffer.length > 0){
			try {
				int received = 0, total = 0;
				while(total < buffer.length){
					received = in.read(buffer, total, buffer.length - total);
					if(received < 0) return StreamResult.EndOfStream;
					total += received;
				}
				return StreamResult.OK;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return StreamResult.ReadException;
	}
	
	public static final <T>T readO(InputStream in, T o){
		return ByteUtils.bytesToObject(read(in, ByteUtils.sizeObject(o)), o);
	}
}
