package org.crococryptfile.suites.pbecamellia;

import org.crococryptfile.suites.pbeaes.PBE1AESMain;
import org.fhissen.crypto.CryptoCodes.BASECIPHER;


public class PBE1CamelliaMain extends PBE1AESMain {
	{
		cipherCode = BASECIPHER.CAMELLIA;
	}
}
