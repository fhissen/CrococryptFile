package org.fhissen.crypto;

import java.util.HashMap;

public class CryptoCodes {
	public static final String KEY_AES = "AES";
	public static final String KEY_RSA = "RSA";
	
	public static final String HMAC_SHA512 = "HmacSHA512";
	public static final String HMAC_WHIRL = "HmacWhirlpool";
	
	public static final String HASH_SHA512 = "SHA512";
	
	public static final int AES_KEYSIZE_BITS = 256;
	public static final int AES_KEYSIZE = AES_KEYSIZE_BITS / 8;

	public static final int STANDARD_BLOCKSIZE = 16;
	public static final int STANDARD_IVSIZE = STANDARD_BLOCKSIZE;
	public static final int STANDARD_SALTSIZE = 64;
	public static final int STANDARD_PBKDF2_ITERATIONS = 100000; 
	public static final int STANDARD_PBKDF2_PWLEN = 8; 


	private static HashMap<BASECIPHER, String> ecbCodes = new HashMap<>();
	private static HashMap<BASECIPHER, String> cbcpadCodes = new HashMap<>();
	private static HashMap<BASECIPHER, String> cbcnopadCodes = new HashMap<>();

	static{
		for(BASECIPHER b: BASECIPHER.values()){
			ecbCodes.put(b, b.getCipherCode() + "/ECB/NoPadding");
			cbcpadCodes.put(b, b.getCipherCode() + "/CBC/PKCS5Padding");
			cbcnopadCodes.put(b, b.getCipherCode() + "/CBC/NoPadding");
		}
	}

	public enum BASECIPHER{
		AES,
		TWOFISH ("Twofish"),
		SERPENT ("Serpent"),
		CAMELLIA ("Camellia"),
		
		;
		
		private String cipherCode = null;
		private BASECIPHER(){}
		private BASECIPHER(String cipherCode){
			this.cipherCode = cipherCode;
		}
		
		private String getCipherCode(){
			if(cipherCode != null)
				return cipherCode;
			
			return name();
		}
		
		public String getECB(){
			return ecbCodes.get(this);
		}
		
		public String getCBCPad(){
			return cbcpadCodes.get(this);
		}
		
		public String getCBCNopad(){
			return cbcnopadCodes.get(this);
		}
	}
}
