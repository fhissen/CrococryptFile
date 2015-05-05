package org.fhissen.crypto;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class InitCrypto {
	private static boolean initialized = false;
	
	public static final void INIT(){
		if(initialized) return;
		
		initialized = true;
		BouncyCastleProvider bc = new BouncyCastleProvider();
		Security.addProvider(bc);
	}

	static{
		INIT();
	}
}
