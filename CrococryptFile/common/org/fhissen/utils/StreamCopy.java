package org.fhissen.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamCopy {
	private final byte buffer[] = new byte[1024 * 1024];

	public boolean copy(InputStream in, OutputStream out) {
		try {
			int bytes;
			while (true) {
				bytes = in.read(buffer);
				if (bytes <= -1) break;
				out.write(buffer, 0, bytes);
			}
			return true;
		} catch (Exception e) {
			System.err.println("error reading or writing: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean copyNclose(InputStream in, OutputStream out) {
		try {
			return copy(in, out);
		}
		finally{
			try {
				in.close();
				out.close();
			} catch (Exception e2) {}
		}
	}
	
	public static final boolean copyNclose(byte[] in, OutputStream out) {
		try {
			out.write(in);
			return true;
		} catch (Exception e) {
			System.err.println("error reading or writing: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}
	}

	public static final String read(InputStream is){
		if(is == null) return null;
		
		byte[] thearray = new byte[1024 * 16];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		int b = 0;
		
		while (true){
			try {
				b = is.read(thearray);
				if (b>=0){
					baos.write(thearray, 0, b);
				}
				else{
					is.close();
					break;
				}
			} catch (Exception e) {
				break;
			}			
		}

		try {
			return new String(baos.toString(Codes.UTF8));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final byte[] readBytes(InputStream is){
		if(is == null) return null;

		byte[] thearray = new byte[1024 * 16];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		int b = 0;
		
		while (true){
			try {
				b = is.read(thearray);
				if (b>=0){
					baos.write(thearray, 0, b);
				}
				else{
					is.close();
					break;
				}
			} catch (Exception e) {
				return null;
			}			
		}

		return baos.toByteArray();
	}
	
	public static final String readUTF8String(InputStream in){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		try {
			byte[] thearray = new byte[1024 * 16];
			int b = 0;
			
			while (true){
					b = in.read(thearray);
					if (b>=0){
						baos.write(thearray, 0, b);
					}
					else{
						break;
					}
			}
			
			return new String(baos.toByteArray(), Codes.UTF8).replace("\r", "");
		}
		catch (Exception e) {
			return null;
		}
		finally{
			try {
				if(in != null) in.close();
			} catch (IOException e) {}
		}
	}
	
	public static final boolean writeBytes(OutputStream os, byte[] bytes){
		try {
			os.write(bytes);
			os.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
