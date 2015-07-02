package org.crococryptfile.suites.pbecloakedaes2f;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import org.crococryptfile.suites.pbecloakedaes2f.PBKDF2Interstep.InterstepChecker;
import org.crococryptfile.ui.cui.CPrint;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.crypto.FSecretKeySpec;
import org.fhissen.crypto.KeygenUtils;
import org.fhissen.crypto.PBKDF2;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.ui.StatusUpdate;


public class PBECloaked_AES2F_PwToKeys {
	private static final int BASE_ITERATIONCOUNT = 50000;
	
	private static final int SECRET_SIZE_BITS = 2 * CryptoCodes.AES_KEYSIZE_BITS;
	private static final int SECRET_SIZE_BYTES = SECRET_SIZE_BITS / 8;
	
	private static final class InterstepCheckerImpl extends InterstepChecker {
		private PBECloaked_AES2F_KeySet key;
		private byte[] org;
		private byte[] encorg;
		private Cipher ciph_aes, ciph_two;
		private final byte[] pwkey_aes;
		private final byte[] pwkey_two;

		
		private InterstepCheckerImpl(PBECloaked_AES2F_KeySet key, byte[] org, byte[] encorg) {
			this.key = key;
			this.org = org;
			this.encorg = encorg;
			
			pwkey_aes = new byte[CryptoCodes.AES_KEYSIZE];
			pwkey_two = new byte[CryptoCodes.AES_KEYSIZE];
			
			try {
				ciph_aes = Cipher.getInstance(CryptoCodes.CIPHER_AES_ECB_NoPad);
				ciph_two = Cipher.getInstance(CryptoCodes.CIPHER_2FISH_ECB_NoPad);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean found(byte[] intermediate_key) {
			if (intermediate_key.length < SECRET_SIZE_BYTES){
				System.err.println("PBEPDPwToKey: Internal error");
				return false;
			}
			
			if (intermediate_key.length > SECRET_SIZE_BYTES){
				System.arraycopy(intermediate_key, 1, pwkey_aes, 0, CryptoCodes.AES_KEYSIZE);
				System.arraycopy(intermediate_key, 1+CryptoCodes.AES_KEYSIZE, pwkey_two, 0, CryptoCodes.AES_KEYSIZE);
			}
			else{
				System.arraycopy(intermediate_key, 0, pwkey_aes, 0, CryptoCodes.AES_KEYSIZE);
				System.arraycopy(intermediate_key, CryptoCodes.AES_KEYSIZE, pwkey_two, 0, CryptoCodes.AES_KEYSIZE);
			}

			byte[] k_plain_aes = ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, false, ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, false, key.aes_enckey));
			byte[] k_plain_two = ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, false, ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, false, key.two_enckey));

			FSecretKeySpec fs_aes = new FSecretKeySpec(k_plain_aes);
			FSecretKeySpec fs_two = new FSecretKeySpec(k_plain_two);
			try {
				ciph_aes.init(Cipher.DECRYPT_MODE, fs_aes);
				ciph_two.init(Cipher.DECRYPT_MODE, fs_two);
				
				if(Arrays.equals(ciph_two.doFinal(ciph_aes.doFinal(encorg)), org)) return true;
			} catch (Exception e2) {}
			finally{
				fs_aes.wipe(true);
				fs_two.wipe(true);
				CryptoUtils.kill(k_plain_aes, k_plain_two);
			}

			return false;
		}
		
		private void deinit(){
			CryptoUtils.kill(pwkey_aes, pwkey_two);

			try {
				ciph_aes.init(Cipher.ENCRYPT_MODE, new FSecretKeySpec(pwkey_aes));
				ciph_two.init(Cipher.ENCRYPT_MODE, new FSecretKeySpec(pwkey_two));
			} catch (Exception e) {}
		}

		@Override
		protected void finalize() throws Throwable {
			deinit();
			super.finalize();
		}
	}

	
	public static final PBECloaked_AES2F_KeySet createPBE(char[] pw){
		try {
			int its = BASE_ITERATIONCOUNT + ((int)(System.currentTimeMillis() % 10000));
			byte[] s = CryptoUtils.randIv(CryptoCodes.STANDARD_SALTSIZE);
			byte[] k_aes = new KeygenUtils().raw(CryptoCodes.AES_KEYSIZE_BITS);
			byte[] k_two = new KeygenUtils().raw(CryptoCodes.AES_KEYSIZE_BITS);
			
			byte[] bbuf = ByteUtils.charsToBytes(pw);
			byte[] pwkey = new PBKDF2(Mac.getInstance(CryptoCodes.HMAC_SHA512)).generateDerivedParameters(SECRET_SIZE_BITS, bbuf, s, its);
			
			byte[] pwkey_aes = new byte[CryptoCodes.AES_KEYSIZE];
			byte[] pwkey_two = new byte[CryptoCodes.AES_KEYSIZE];
			
			System.arraycopy(pwkey, 0, pwkey_aes, 0, pwkey_aes.length);
			System.arraycopy(pwkey, pwkey_aes.length, pwkey_two, 0, pwkey_two.length);
			
			CryptoUtils.kill(bbuf, pwkey);
			CryptoUtils.kill(pw);

			byte[] k_enc_aes = ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, true, ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, true, k_aes));
			byte[] k_enc_two = ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, true, ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, true, k_two));
			
			CryptoUtils.kill(pwkey_aes, pwkey_two);
			its = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

			PBECloaked_AES2F_KeySet key = new PBECloaked_AES2F_KeySet();
			key.aes_enckey = k_enc_aes;
			key.aes_plainkey = k_aes;
			key.two_enckey = k_enc_two;
			key.two_plainkey = k_two;
			key.salt = s;

			return key;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final void loadPBE(char[] pw, PBECloaked_AES2F_KeySet key, byte[] org, byte[] encorg, StatusUpdate status){
		if(pw == null || pw.length == 0) return;
		try {
			byte[] bbuf = ByteUtils.charsToBytes(pw);
			InterstepCheckerImpl inter = new InterstepCheckerImpl(key, org, encorg);
			byte[] pwkey = new PBKDF2Interstep(Mac.getInstance(CryptoCodes.HMAC_SHA512), inter, status).generateDerivedParameters(SECRET_SIZE_BITS, bbuf, key.salt);
			inter.deinit();
			
			byte[] pwkey_aes = new byte[CryptoCodes.AES_KEYSIZE];
			byte[] pwkey_two = new byte[CryptoCodes.AES_KEYSIZE];
			
			System.arraycopy(pwkey, 0, pwkey_aes, 0, pwkey_aes.length);
			System.arraycopy(pwkey, pwkey_aes.length, pwkey_two, 0, pwkey_two.length);
			
			byte[] k_plain_aes = ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, false, ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, false, key.aes_enckey));
			byte[] k_plain_two = ecbRaw(pwkey_aes, CryptoCodes.CIPHER_AES_ECB_NoPad, false, ecbRaw(pwkey_two, CryptoCodes.CIPHER_2FISH_ECB_NoPad, false, key.two_enckey));
			
			CryptoUtils.kill(pwkey_aes, pwkey_two);
			CryptoUtils.kill(bbuf, pwkey);
			CryptoUtils.kill(pw);

			key.aes_plainkey = k_plain_aes;
			key.two_plainkey = k_plain_two;
			
			if(status != null && status.isActive()) CPrint.line("Decryption key found for cloaked file");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] ecbRaw(byte[] rawkey, String ciphercode, boolean encrypt, byte[] in){
		try {
			FSecretKeySpec fkey = new FSecretKeySpec(rawkey);
			Cipher ciph = Cipher.getInstance(ciphercode);
			if(encrypt)
				ciph.init(Cipher.ENCRYPT_MODE, fkey);
			else
				ciph.init(Cipher.DECRYPT_MODE, fkey);
			
			byte[] ret = ciph.doFinal(in);

			fkey.wipe(false);
			ciph.init(Cipher.ENCRYPT_MODE, new FSecretKeySpec(CryptoUtils.randIv16()));
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
