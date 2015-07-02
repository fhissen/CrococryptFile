package org.crococryptfile.suites.pbecloakedaes2f;

import org.fhissen.crypto.CryptoUtils;

public class PBECloaked_AES2F_KeySet{
	public byte[] aes_enckey, aes_plainkey;
	public byte[] two_enckey, two_plainkey;
	public byte[] salt;
	
	public void deinit(){
		CryptoUtils.kill(aes_enckey, aes_plainkey, two_enckey, two_plainkey, salt);
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
}
