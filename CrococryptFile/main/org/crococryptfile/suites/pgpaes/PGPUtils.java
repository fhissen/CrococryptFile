package org.crococryptfile.suites.pgpaes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.fhissen.utils.os.OSFolders;


public class PGPUtils {
	private static final String FOLDER = "gnupg";
	private static final String FILE_PUB = "pubring.gpg";
	private static final String FILE_PRIV = "secring.gpg";
	
	public static final File getPubRing(){
		File tmp = OSFolders.getUserAppfolder(FOLDER);
		tmp = new File(tmp, FILE_PUB);
		if(tmp.exists() && tmp.isFile()) return tmp;
		
		return null;
	}
	
	public static final File getPrivRing(){
		File tmp = OSFolders.getUserAppfolder(FOLDER);
		tmp = new File(tmp, FILE_PRIV);
		if(tmp.exists() && tmp.isFile()) return tmp;
		
		return null;
	}
	
	public static final byte[] encrypt(File pubring, long keyid, byte[] in){
		if(!pubring.exists() || !pubring.isFile()) return null;
		
		try {
			byte[] ret = null;
			
			FileInputStream fis = new FileInputStream(pubring);
			InputStream is = PGPUtil.getDecoderStream(fis);
	        PGPPublicKeyRingCollection ring = new PGPPublicKeyRingCollection(is);
	        PGPPublicKey pubkey = ring.getPublicKey(keyid);
			if(pubkey.isMasterKey()) {
				System.err.println("Tried to use a non-encryption key. This should never happen.");
				return null;
			}

	        PublicKey key = new JcaPGPKeyConverter().getPublicKey(pubkey);
		    Cipher cipher = Cipher.getInstance(key.getAlgorithm() + "/ECB/PKCS1Padding");
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    ret = cipher.doFinal(in);
	        
	        is.close();
	        fis.close();
			
			return ret;
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static final byte[] decrypt(File privring, long keyid, char[] pw, byte[] in){
		if(!privring.exists() || !privring.isFile()) return null;
		
		try {
			byte[] ret = null;
			
			FileInputStream fis = new FileInputStream(privring);
			InputStream is = PGPUtil.getDecoderStream(fis);
			PGPSecretKeyRingCollection ring = new PGPSecretKeyRingCollection(is);
	        PGPSecretKey seckey = ring.getSecretKey(keyid);
			if(seckey.isMasterKey()) {
				System.err.println("Someone tried to use a non-encryption key. This should never happen.");
				return null;
			}
			
	        PrivateKey key = new JcaPGPKeyConverter().getPrivateKey(
	        					seckey.extractPrivateKey(
	        						new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pw)
	        					)
            				);
	        
		    Cipher cipher = Cipher.getInstance(key.getAlgorithm() + "/ECB/PKCS1Padding");
		    cipher.init(Cipher.DECRYPT_MODE, key);
		    ret = cipher.doFinal(in);
	        
	        is.close();
	        fis.close();
			
			return ret;
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}
	
	public static class PGPSecretKeyFinder{
		private FileInputStream fis;
		private InputStream dis;
		private PGPSecretKeyRingCollection ring;
		
		public PGPSecretKeyFinder(String keyfile){
			this(new File(keyfile));
		}
		
		public PGPSecretKeyFinder(File keyfile){
			if(!keyfile.exists() || !keyfile.isFile()) return;
			
			try {
				fis = new FileInputStream(keyfile);
				dis = PGPUtil.getDecoderStream(fis);
				ring = new PGPSecretKeyRingCollection(dis);
			} catch (Exception e) {
				System.err.println(e.getLocalizedMessage());
			}
		}
		
		public boolean validFile(){
			return ring != null;
		}
		
		public boolean exists(long keyid){
			if(ring == null) return false;
			
			try {
				return ring.getSecretKey(keyid) != null;
			} catch (Exception e) {
				return false;
			}
		}
		
		public void close(){
			if(fis != null){
				try {
					fis.close();
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
