package org.fhissen.crypto;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import org.fhissen.crypto.CryptoCodes.BASECIPHER;


public class CryptoUtils {
	static{
		InitCrypto.INIT();
	}
	
	private static Random randio = new Random();
	static{
		randio.nextBoolean();
		long l = randio.nextLong();
		randio.setSeed(System.currentTimeMillis() ^ l);
	}

	public static final void kill(byte[] b){
		if(b == null) return;
		randio.nextBytes(b);
	}

	public static final void kill(byte[]... bs){
		if(bs == null) return;
		
		for(byte[]b: bs)
			kill(b);
	}

	public static final void kill(char[] b){
		if(b == null) return;
		for(int i=0; i<b.length; i++)
			b[i] = (char)randio.nextInt();
	}
	

	private static final SecureRandom sr = new KeygenUtils().makeSR();
	public static final byte[] randIv16(){
		byte[] ret = new byte[16];
		sr.nextBytes(ret);
		return ret;
	}

	public static final byte[] randIv(int size){
		byte[] ret = new byte[size];
		sr.nextBytes(ret);
		return ret;
	}
	
	
	public static final long makeSimpleId(){
		return UUID.randomUUID().getMostSignificantBits();
	}
	
	
	public static final String[] toArray(String... s){
		return s;
	}

	public static final BASECIPHER[] toArray(BASECIPHER... bc){
		return bc;
	}

	public static final FSecretKeySpec[] toArray(FSecretKeySpec... fs){
		return fs;
	}

	public static final FSecretKeySpec[] toArray(byte[]... rawkeys){
		if(rawkeys == null || rawkeys.length == 0) return null;
		
		FSecretKeySpec[] fs = new FSecretKeySpec[rawkeys.length];
		
		for(int i=0; i<rawkeys.length; i++){
			fs[i] = new FSecretKeySpec(rawkeys[i]);
		}
		
		return fs;
	}
}
