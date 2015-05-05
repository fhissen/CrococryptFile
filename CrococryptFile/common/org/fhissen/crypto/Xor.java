package org.fhissen.crypto;

import java.util.Arrays;


public class Xor {
	public static byte[] xor(byte[]a, byte[]b){
		int max = a.length;
		byte[] buf;
		
		if(a.length > b.length){
			max = b.length;
			buf = Arrays.copyOf(a, a.length);
		}
		else{
			buf = Arrays.copyOf(b, b.length);
		}
		
		for(int i=0; i<max; i++){
			buf[i] = (byte) (a[i] ^ b[i]);
		}
		
		return buf;
	}

	public static byte[] xorWipe(byte[]a, byte[]b){
		int max = a.length;
		byte[] buf;
		
		if(a.length > b.length){
			max = b.length;
			buf = Arrays.copyOf(a, a.length);
		}
		else{
			buf = Arrays.copyOf(b, b.length);
		}
		
		for(int i=0; i<max; i++){
			buf[i] = (byte) (a[i] ^ b[i]);
		}
		
		CryptoUtils.kill(a);
		CryptoUtils.kill(b);
		
		return buf;
	}
}
