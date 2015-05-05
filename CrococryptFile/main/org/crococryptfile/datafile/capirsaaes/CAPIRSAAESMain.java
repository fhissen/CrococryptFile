package org.crococryptfile.datafile.capirsaaes;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;

import javax.crypto.Cipher;

import org.crococryptfile.CrococryptFile;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.SuiteMODE;
import org.crococryptfile.suites.SuitePARAM;
import org.crococryptfile.ui.resources._T;
import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoCodes.BASECIPHER;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.crypto.FSecretKeySpec;
import org.fhissen.crypto.KeygenUtils;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.Codes;
import org.fhissen.utils.StreamMachine;


public class CAPIRSAAESMain extends Suite {
	private static KeyStore capi = CAPIRSAUtils.getCAPI();

	private int ATTRIBUTE_DNLEN; 
	private byte[] ATTRIBUTE_DN; 
	private int ATTRIBUTE_KEYLEN; 
	private byte[] ATTRIBUTE_KEY; 
	private byte[] ATTRIBUTE_IV = new byte[CryptoCodes.STANDARD_IVSIZE]; 
	
	private String alias;
	private byte[] aesplain;
	private CipherMain ciph;

	public CAPIRSAAESMain(){
		super(SUITES.CAPI_RSAAES);
	}

	
	@Override
	protected void _init(SuiteMODE mode, HashMap<SuitePARAM, Object> params) throws IllegalArgumentException{
		if(mode == SuiteMODE.ENCRYPT){
			if(params == null || params.size() == 0)  throw new IllegalArgumentException("CAPI: no params specified");
			alias = (String) params.get(SuitePARAM.capi_alias);
			if(alias == null) throw new IllegalArgumentException("CAPIEncrypt mode must specify an alias");

			aesplain = new KeygenUtils().raw(CryptoCodes.AES_KEYSIZE_BITS);
			ciph = new CipherMain(BASECIPHER.AES, new FSecretKeySpec(aesplain));
			ATTRIBUTE_IV = CryptoUtils.randIv16();
			
			try {
				ATTRIBUTE_DN = CAPIRSAUtils.getDNFromAlias(alias).getBytes(Codes.UTF8);
				ATTRIBUTE_DNLEN = ATTRIBUTE_DN.length;
				ATTRIBUTE_KEY = enc(aesplain);
				ATTRIBUTE_KEYLEN = ATTRIBUTE_KEY.length;
			} catch (Exception e) {
				 throw new IllegalArgumentException("CAPIEncryption not supported, probably an unusable key alias: " + e.getLocalizedMessage());
			}
		}
	}
	
	private byte[] enc(byte[] plainaes) throws Exception{
		Certificate cert = capi.getCertificate(alias);
	    Cipher cipher = Cipher.getInstance(CAPIRSAUtils.CAPI_RSACIPHER, CAPIRSAUtils.CAPI_PROVIDER);
	    cipher.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());
	    return cipher.doFinal(plainaes);
	}
	
	private byte[] dec(byte[] encaes) throws Exception{
		PrivateKey key = (PrivateKey)capi.getKey(alias, null);
	    Cipher cipher = Cipher.getInstance(CAPIRSAUtils.CAPI_RSACIPHER, CAPIRSAUtils.CAPI_PROVIDER);
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    return cipher.doFinal(encaes);
	}
	
	
	@Override
	protected void _writeTo(OutputStream out) throws IllegalStateException{
		try {
			StreamMachine.write(out,
					ATTRIBUTE_DNLEN,
					ATTRIBUTE_DN,
					ATTRIBUTE_KEYLEN,
					ATTRIBUTE_KEY,
					ciph.doEnc_ECB(ATTRIBUTE_IV)
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void _readFrom(InputStream is) throws IllegalStateException{
		StreamMachine sm = new StreamMachine(is);

		ATTRIBUTE_DNLEN = sm.readO(ATTRIBUTE_DNLEN);
		ATTRIBUTE_DN = new byte[ATTRIBUTE_DNLEN];
		sm.read(ATTRIBUTE_DN);
		
		ATTRIBUTE_KEYLEN = sm.readO(ATTRIBUTE_KEYLEN);
		ATTRIBUTE_KEY = new byte[ATTRIBUTE_KEYLEN];
		sm.read(ATTRIBUTE_KEY);
		
		String dn = null;
		try {
			dn = new String(ATTRIBUTE_DN, Codes.UTF8);
			alias = CAPIRSAUtils.getAliasFromDN(dn);
			aesplain = dec(ATTRIBUTE_KEY);
			ciph = new CipherMain(BASECIPHER.AES, new FSecretKeySpec(aesplain));
		} catch (Exception e) {
			CrococryptFile.LASTERROR = _T.CAPI_DNnotfound.msg(dn);
			e.printStackTrace();
		}
		
		sm.read(ATTRIBUTE_IV);
		
		ATTRIBUTE_IV = ciph.doDec_ECB(ATTRIBUTE_IV);
	}
	

	@Override
	public CipherMain getCipher(){
		return ciph;
	}
	
	@Override
	public final int headerLength(){
		return ByteUtils.sizeInBytes(ATTRIBUTE_DNLEN, ATTRIBUTE_DN, ATTRIBUTE_KEYLEN, ATTRIBUTE_KEY, ATTRIBUTE_IV);
	}
	
	@Override
	public byte[] getAttributeIV(){
		return ATTRIBUTE_IV;
	}
	
	@Override
	public final void deinit(){
		CryptoUtils.kill(aesplain, ATTRIBUTE_KEY, ATTRIBUTE_IV);
		ciph.deinint();
		System.gc();
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
}
