package org.crococryptfile.suites.pbeaes;

import org.fhissen.crypto.CryptoUtils;

public class PBE1AESKeySet{
	public byte[] enckey, plainkey;
	public int its;
	public byte[] salt;
	
	public void deinit(){
		CryptoUtils.kill(enckey, plainkey, salt);
		its = -1;
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
}
