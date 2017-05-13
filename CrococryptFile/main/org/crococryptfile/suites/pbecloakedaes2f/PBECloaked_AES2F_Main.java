package org.crococryptfile.suites.pbecloakedaes2f;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.SuiteMODE;
import org.crococryptfile.suites.SuitePARAM;
import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoCodes.BASECIPHER;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.StreamMachine;


public class PBECloaked_AES2F_Main extends Suite {
	private byte[] ATTRIBUTE_SALT = new byte[CryptoCodes.STANDARD_SALTSIZE];
	private byte[] ATTRIBUTE_SALTENC = new byte[CryptoCodes.STANDARD_SALTSIZE];
	private byte[] ATTRIBUTE_KEY_AES = new byte[CryptoCodes.AES_KEYSIZE];
	private byte[] ATTRIBUTE_KEY_TWO = new byte[CryptoCodes.AES_KEYSIZE];
	private byte[] ATTRIBUTE_IV = new byte[CryptoCodes.STANDARD_IVSIZE];

	private PBECloaked_AES2F_KeySet key;
	private CipherMain ciph;
	private char[] pw;

	@Override
	public final void deinit(){
		if(key != null) key.deinit();
		key = null;
		CryptoUtils.kill(pw);
		CryptoUtils.kill(ATTRIBUTE_KEY_AES, ATTRIBUTE_KEY_TWO, ATTRIBUTE_IV);
		ciph.deinint();
		System.gc();
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
	
	private final int len = ByteUtils.sizeInBytes(ATTRIBUTE_SALT, ATTRIBUTE_SALTENC, ATTRIBUTE_KEY_AES, ATTRIBUTE_KEY_TWO, ATTRIBUTE_IV);
	public final int headerLength(){
		return len;
	}
	
	public CipherMain getCipher(){
		return ciph;
	}

	@Override
	protected void _init(SuiteMODE mode, HashMap<SuitePARAM, Object> params) throws IllegalArgumentException{
		if(params == null || params.size() == 0)  throw new IllegalArgumentException("PBE: no params specified");
		pw = (char[])params.get(SuitePARAM.password);
		if(pw == null) throw new IllegalArgumentException("PBE must specify a password");
		
		int itcount_ext = 0;
		if(params.containsKey(SuitePARAM.itcount)){
			try {
				itcount_ext = Integer.parseInt((String)params.get(SuitePARAM.itcount));
			} catch (Exception e) {}
		}

		if(mode == SuiteMODE.ENCRYPT){
			key = PBECloaked_AES2F_PwToKeys.createPBE(pw, itcount_ext, getStatus());
			
			ciph = CipherMain.instance(CryptoUtils.toArray(BASECIPHER.TWOFISH, BASECIPHER.AES), key.two_plainkey, key.aes_plainkey);
			
			ATTRIBUTE_SALT = key.salt;
			ATTRIBUTE_SALTENC = ciph.doEnc_ECB(ATTRIBUTE_SALT);
			
			ATTRIBUTE_KEY_AES = key.aes_enckey;
			ATTRIBUTE_KEY_TWO = key.two_enckey;
			ATTRIBUTE_IV = CryptoUtils.randIv16();
		}
	}
	
	protected void _writeTo(OutputStream out) throws IllegalStateException{
		try {
			StreamMachine.write(out,
					ATTRIBUTE_SALT,
					ATTRIBUTE_SALTENC,
					ATTRIBUTE_KEY_AES,
					ATTRIBUTE_KEY_TWO,
					ciph.doEnc_ECB(ATTRIBUTE_IV)
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void _readFrom(InputStream is) throws IllegalStateException{
		StreamMachine sm = new StreamMachine(is);

		sm.read(ATTRIBUTE_SALT);
		sm.read(ATTRIBUTE_SALTENC);
		sm.read(ATTRIBUTE_KEY_AES);
		sm.read(ATTRIBUTE_KEY_TWO);
		sm.read(ATTRIBUTE_IV);
		
		if(ciph == null){
			key = new PBECloaked_AES2F_KeySet();
			key.aes_enckey = ATTRIBUTE_KEY_AES;
			key.two_enckey = ATTRIBUTE_KEY_TWO;
			key.salt = ATTRIBUTE_SALT;
			
			PBECloaked_AES2F_PwToKeys.loadPBE(pw, key, ATTRIBUTE_SALT, ATTRIBUTE_SALTENC, getStatus());
			ciph = CipherMain.instance(CryptoUtils.toArray(BASECIPHER.TWOFISH, BASECIPHER.AES), key.two_plainkey, key.aes_plainkey);
		}
		
		ATTRIBUTE_IV = ciph.doDec_ECB(ATTRIBUTE_IV);
	}
	
	public byte[] getAttributeIV(){
		return ATTRIBUTE_IV;
	}
	
	public boolean fill(){
		return false;
	}
}
