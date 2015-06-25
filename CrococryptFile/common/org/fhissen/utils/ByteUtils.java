package org.fhissen.utils;

import java.nio.ByteBuffer;

import org.fhissen.crypto.CryptoUtils;


public class ByteUtils {
	public static final byte[] stringToBytes(String str) {
		byte[] b = new byte[str.length() << 1];
		for (int i = 0; i < str.length(); i++) {
			char strChar = str.charAt(i);
			int bpos = i << 1;
			b[bpos] = (byte) ((strChar & 0xFF00) >> 8);
			b[bpos + 1] = (byte) (strChar & 0x00FF);
		}
		return b;
	}
	
	public static final byte[] charsToBytes(char[] chars) {
		byte[] b = new byte[chars.length << 1];
		for (int i = 0; i < chars.length; i++) {
			char strChar = chars[i];
			int bpos = i << 1;
			b[bpos] = (byte) ((strChar & 0xFF00) >> 8);
			b[bpos + 1] = (byte) (strChar & 0x00FF);
		}
		return b;
	}
	
	public static final char[] bytesToChars(byte[] bytes) {
		char[] buffer = new char[bytes.length >> 1];
		for (int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			char c = (char) (((bytes[bpos] & 0x00FF) << 8) + (bytes[bpos + 1] & 0x00FF));
			buffer[i] = c;
		}
		
		return buffer;
	}

	public static final String bytesToString(byte[] bytes) {
		return new String(bytesToChars(bytes));
	}
	
	
	public static final byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
	    buffer.putLong(x);
	    return buffer.array();
	}

	public static final long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
	    buffer.put(bytes);
	    buffer.flip(); 
	    return buffer.getLong();
	}

	
	public static final byte[] intToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	    buffer.putInt(x);
	    return buffer.array();
	}

	public static final int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	    buffer.put(bytes);
	    buffer.flip(); 
	    return buffer.getInt();
	}
	
	public static final byte[] objToBytes(Object o){
		JTYPE type = null;
		try {
			type = typeFromObj(o);
		} catch (Exception e) {
			System.err.println("OBJ unknown");
			return null;
		}
		
	    ByteBuffer buffer = ByteBuffer.allocate(type.size());
		switch (type) {
		case Long:
			buffer.putLong((Long)o);
			break;
		case Integer:
			buffer.putInt((Integer)o);
			break;
		case Byte:
			buffer.put((Byte)o);
			break;
		case Short:
			buffer.putShort((Short)o);
			break;
		case Double:
			buffer.putDouble((Double)o);
			break;
		case Float:
			buffer.putFloat((Float)o);
			break;
		case Character:
			buffer.putChar((char)o);
			break;
		default:
			System.err.println("OBJ unknown");
		}

	    return buffer.array();
	}
	
	@SuppressWarnings("unchecked")
	public static final <T>T bytesToObject(byte[] bytes, T o){
		JTYPE type = null;
		try {
			type = typeFromObj(o);
		} catch (Exception e) {
			System.err.println("OBJ unknown");
			return null;
		}
		
		if(bytes.length > type.size) bytes = cut(bytes, type.size);
		
	    ByteBuffer buffer = ByteBuffer.allocate(type.size());
	    buffer.put(bytes);
	    buffer.flip(); 
	    
		switch (type) {
		case Long:
			return (T) (Long)buffer.getLong();
		case Integer:
			return (T) (Integer)buffer.getInt();
		case Byte:
			return (T) (Byte)buffer.get();
		case Short:
			return (T) (Short)buffer.getShort();
		case Double:
			return (T) (Double)buffer.getDouble();
		case Float:
			return (T) (Float)buffer.getFloat();
		case Character:
			return (T) (Character)buffer.getChar();
		default:
			System.err.println("OBJ unknown");
			return null;
		}
	}
	
	public enum JTYPE{
		Long(8),
		Integer(4),
		Byte(1),
		Short(2),
		Double(64),
		Float(32),
		Character(2),
		
		;
		
		private int size;
		private JTYPE(int size){
			this.size = size;
		}
		
		public int size(){
			return size;
		}
	}
	
	public static final JTYPE typeFromObj(Object obj){
		JTYPE type = null;
		try {
			type = JTYPE.valueOf(obj.getClass().getSimpleName());
		} catch (Exception e) {
		}
		return type;
	}
	
	public static final int sizeObject(Object o){
		return typeFromObj(o).size();
	}
	
	public static final int sizeInBytes(Object o){
		if(o instanceof byte[]) return ((byte[]) o).length;
		return typeFromObj(o).size();
	}
	
	public static final int sizeInBytes(Object... o){
		int ret = 0;
		for(Object ox: o)
			ret += sizeInBytes(ox);
		return ret;
	}
	
	public static final int b2i(byte b){
		if(b >= 0) return b;
		return b + 256;
	}
	
	public static final byte i2b(int i){
		if(i > 128) return (byte)(i - 256);
		return (byte) i;
	}
	
	public static final byte[] copyTogetherNwipeO(Object... arr){
		byte[][] b = new byte[arr.length][];
		
		for(int i=0; i<arr.length; i++){
			if(arr[i] != null)
				b[i] = objToBytes(arr[i]);
			else
				b[i] = new byte[]{1};
		}
		
		return copyTogetherNwipeB(b);
	}
	
	public static final byte[] copyTogetherNwipeB(byte[]... arr){
		int len = 0;
		for(byte[] b: arr){
			if(b == null) continue;
			
			len += b.length;
		}
		
		byte[] ret = new byte[len];
		int start = 0;
		for(byte[] b: arr){
			if(b == null) continue;
			
			System.arraycopy(b, 0, ret, start, b.length);
			start += b.length;
			CryptoUtils.kill(b);
		}
		
		return ret;
	}

	public static final byte[] cut(byte[] bytes, int newlen){
		byte[] ret = new byte[newlen];
		System.arraycopy(bytes, 0, ret, 0, newlen);
		return ret;
	}
	
	public static final byte[] fill(byte[] bytes, int newlen){
		byte[] ret = new byte[newlen];
		System.arraycopy(bytes, 0, ret, 0, bytes.length);
		return ret;
	}
	
	public static final byte[] fill(Object o, int newlen){
		byte[] o_bytes = objToBytes(o);
		byte[] ret = new byte[newlen];
		System.arraycopy(o_bytes, 0, ret, 0, o_bytes.length);
		return ret;
	}
}
