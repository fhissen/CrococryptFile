package org.crococryptfile.suites.pbeaes;

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


public class PBE1AESMain extends Suite {
	private int ATTRIBUTE_ITERATIONCOUNT; 
	private byte[] ATTRIBUTE_SALT = new byte[CryptoCodes.STANDARD_SALTSIZE]; 
	private byte[] ATTRIBUTE_KEY = new byte[CryptoCodes.AES_KEYSIZE]; 
	private byte[] ATTRIBUTE_IV = new byte[CryptoCodes.STANDARD_IVSIZE]; 
	
	@Override
	public final void deinit(){
		if(key != null) key.deinit();
		key = null;
		CryptoUtils.kill(pw);
		CryptoUtils.kill(ATTRIBUTE_KEY, ATTRIBUTE_IV);
		ciph.deinint();
		System.gc();
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
	
	private final int len = ByteUtils.sizeInBytes(ATTRIBUTE_ITERATIONCOUNT, ATTRIBUTE_SALT, ATTRIBUTE_KEY, ATTRIBUTE_IV);
	public final int headerLength(){
		return len;
	}
	
	public CipherMain getCipher(){
		return ciph;
	}
	

	private PBE1AESKeySet key;
	private CipherMain ciph;
	private char[] pw;
	

	protected BASECIPHER cipherCode = BASECIPHER.AES;
	
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
			key = PBE1PwToKey.createPBE(pw, itcount_ext, getStatus());
			ciph = CipherMain.instance(cipherCode, key.plainkey);
			
			ATTRIBUTE_ITERATIONCOUNT = key.its;
			ATTRIBUTE_SALT = key.salt;
			ATTRIBUTE_KEY = key.enckey;
			ATTRIBUTE_IV = CryptoUtils.randIv16();
		}
	}
	

	protected void _writeTo(OutputStream out) throws IllegalStateException{
		try {
			StreamMachine.write(out,
					ATTRIBUTE_ITERATIONCOUNT,
					ATTRIBUTE_SALT,
					ATTRIBUTE_KEY,
					ciph.doEnc_ECB(ATTRIBUTE_IV)
					);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void _readFrom(InputStream is) throws IllegalStateException{
		StreamMachine sm = new StreamMachine(is);

		ATTRIBUTE_ITERATIONCOUNT = sm.readO(ATTRIBUTE_ITERATIONCOUNT);
		sm.read(ATTRIBUTE_SALT);
		sm.read(ATTRIBUTE_KEY); 
		sm.read(ATTRIBUTE_IV);
		
		if(ciph == null){
			key = new PBE1AESKeySet();
			key.enckey = ATTRIBUTE_KEY;
			key.its = ATTRIBUTE_ITERATIONCOUNT;
			key.salt = ATTRIBUTE_SALT;
			
			PBE1PwToKey.loadPBE(pw, key, getStatus());

			ciph = CipherMain.instance(cipherCode, key.plainkey);
		}
		
		ATTRIBUTE_IV = ciph.doDec_ECB(ATTRIBUTE_IV);
	}
	
	
	public byte[] getAttributeIV(){
		return ATTRIBUTE_IV;
	}
}
