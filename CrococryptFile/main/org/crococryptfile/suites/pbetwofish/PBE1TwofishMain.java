package org.crococryptfile.suites.pbetwofish;

import org.crococryptfile.suites.pbeaes.PBE1AESMain;
import org.fhissen.crypto.CryptoCodes.BASECIPHER;


public class PBE1TwofishMain extends PBE1AESMain {
	{
		cipherCode = BASECIPHER.TWOFISH;
	}
}
