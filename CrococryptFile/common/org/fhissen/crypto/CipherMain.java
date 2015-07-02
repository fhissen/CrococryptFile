package org.fhissen.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.fhissen.crypto.CryptoCodes.BASECIPHER;


public class CipherMain {
	private FSecretKeySpec[] keys;
	private BASECIPHER[] ciphers;
	
	public CipherMain(BASECIPHER cipher, FSecretKeySpec key) throws IllegalArgumentException {
		this(new BASECIPHER[]{cipher}, new FSecretKeySpec[]{key});
	}
	
	public CipherMain(BASECIPHER[] ciphers, FSecretKeySpec[] keys) throws IllegalArgumentException {
		if(ciphers == null || ciphers.length == 0 || keys == null || keys.length == 0 || keys.length != ciphers.length)
			throw new IllegalArgumentException("Key options not valid");

		this.keys = keys;
		this.ciphers = ciphers;
	}


	public OutputStream createOS_CBC_Pad(OutputStream os, byte[] iv){
		try {
			Cipher[] ciphs = generateCBCPadCiphers(false, iv);
			CipherOutputStream cos = new CipherOutputStream(os, ciphs[0]);
			for(int i=1; i<ciphs.length; i++){
				cos = new CipherOutputStream(cos, ciphs[i]);
			}
			
			return cos;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public InputStream createIS_CBC_Pad(InputStream is, byte[] iv){
		try {
			Cipher[] ciphs = generateCBCPadCiphers(true, iv);
			CipherInputStream cis = new CipherInputStream(is, ciphs[0]);
			for(int i=1; i<ciphs.length; i++){
				cis = new CipherInputStream(cis, ciphs[i]);
			}
			
			return cis;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	

	public byte[] doEnc_ECB(byte[] in){
		try {
			Cipher[] ciphs = generateECBCiphers(false);
			byte[] ret = ciphs[0].doFinal(in);
			for(int i=1; i<ciphs.length; i++){
				ret = ciphs[i].doFinal(ret);
			}
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public byte[] doDec_ECB(byte[] in){
		try {
			Cipher[] ciphs = generateECBCiphers(true);
			
			if(ciphs.length == 1) return ciphs[0].doFinal(in);
			
			byte[] ret = ciphs[ciphs.length - 1].doFinal(in);
			for(int i=ciphs.length-2; i>=0; i--){
				ret = ciphs[i].doFinal(ret);
			}
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public void deinint(){
		for(FSecretKeySpec fs: keys)
			fs.wipe(true);
		
		keys = null;
		ciphers = null;
	}

	private Cipher[] generateECBCiphers(boolean decrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException{
		Cipher[] ciphs = new Cipher[keys.length];
		for(int i=0; i<keys.length; i++){
			ciphs[i] = Cipher.getInstance(ciphers[i].getECB());
			if(decrypt) ciphs[i].init(Cipher.DECRYPT_MODE, keys[i]);
			else ciphs[i].init(Cipher.ENCRYPT_MODE, keys[i]);
		}
		return ciphs;
	}


	private Cipher[] generateCBCPadCiphers(boolean decrypt, byte[] iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException{
		Cipher[] ciphs = new Cipher[keys.length];
		for(int i=0; i<keys.length; i++){
			ciphs[i] = Cipher.getInstance(ciphers[i].getCBCPad());
			if(decrypt) ciphs[i].init(Cipher.DECRYPT_MODE, keys[i], new IvParameterSpec(iv));
			else ciphs[i].init(Cipher.ENCRYPT_MODE, keys[i], new IvParameterSpec(iv));
		}
		return ciphs;
	}

	@Override
	protected void finalize() throws Throwable {
		deinint();
		super.finalize();
	}
	
	
	public static final CipherMain instance(BASECIPHER cipher, byte[] key){
		try {
			return new CipherMain(CryptoUtils.toArray(cipher), CryptoUtils.toArray(key));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static final CipherMain instance(BASECIPHER[] ciphers, byte[]... keys){
		try {
			return new CipherMain(ciphers, CryptoUtils.toArray(keys));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
