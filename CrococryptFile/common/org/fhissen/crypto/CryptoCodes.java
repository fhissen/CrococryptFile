package org.fhissen.crypto;

public class CryptoCodes {
	public static final String KEY_AES = "AES";
	public static final String KEY_RSA = "RSA";
	
	public static final String CIPHER_AES_ECB_NoPad = "AES/ECB/NoPadding";
	public static final String CIPHER_AES_CBC_Pad = "AES/CBC/PKCS5Padding";
	public static final String CIPHER_AES_CBC_NoPad = "AES/CBC/NoPadding";
	public static final String CIPHER_2FISH_ECB_NoPad = "Twofish/ECB/NoPadding";
	public static final String CIPHER_2FISH_CBC_Pad = "Twofish/CBC/PKCS5Padding";
	public static final String CIPHER_2FISH_CBC_NoPad = "Twofish/CBC/NoPadding";
	
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


	public enum BASECIPHER{
		AES,
		TWOFISH,
		
		;
		
		
		public String getECB(){
			switch (this) {
			case AES:
				return CIPHER_AES_ECB_NoPad;
			case TWOFISH:
				return CIPHER_2FISH_ECB_NoPad;
			}
			
			return null;
		}
		
		public String getCBCPad(){
			switch (this) {
			case AES:
				return CIPHER_AES_CBC_Pad;
			case TWOFISH:
				return CIPHER_2FISH_CBC_Pad;
			}
			
			return null;
		}
		
		public String getCBCNopad(){
			switch (this) {
			case AES:
				return CIPHER_AES_CBC_NoPad;
			case TWOFISH:
				return CIPHER_2FISH_CBC_NoPad;
			}
			
			return null;
		}
	}
}
