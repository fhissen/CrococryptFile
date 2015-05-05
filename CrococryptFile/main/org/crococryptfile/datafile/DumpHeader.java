package org.crococryptfile.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.crococryptfile.suites.Suite;
import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.Xor;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.StreamMachine;

public class DumpHeader {
	public static final int DUMPHEADER_VERSION = 1;
	
	private int ATTRIBUTE_VERSION = DUMPHEADER_VERSION;
	public long ATTRIBUTE_DUMPLEN;
	public long ATTRIBUTE_DUMPCOUNT;
	
	private Suite suite;
	private CipherMain ciph;
	
	public DumpHeader(Suite suite){
		this.suite = suite;
		ciph = suite.getCipher();
	}
	
	public void createOut(OutputStream out) throws IOException{
		byte[] tmp;
		
		tmp = ByteUtils.fill(ATTRIBUTE_VERSION, CryptoCodes.STANDARD_BLOCKSIZE);
		tmp = Xor.xor(suite.getAlteredIV(2), tmp);
		out.write(ciph.doEnc_ECB(tmp));
		
		tmp = ByteUtils.fill(ATTRIBUTE_DUMPLEN, CryptoCodes.STANDARD_BLOCKSIZE);
		tmp = Xor.xor(suite.getAlteredIV(4), tmp);
		out.write(ciph.doEnc_ECB(tmp));
		
		tmp = ByteUtils.fill(ATTRIBUTE_DUMPCOUNT, CryptoCodes.STANDARD_BLOCKSIZE);
		tmp = Xor.xor(suite.getAlteredIV(6), tmp);
		out.write(ciph.doEnc_ECB(tmp));
	}
	
	public void readFrom(InputStream is) {
		byte[] tmp;
		StreamMachine sm = new StreamMachine(is);
		
		tmp = sm.read(CryptoCodes.STANDARD_BLOCKSIZE);
		ATTRIBUTE_VERSION = ByteUtils.bytesToObject(Xor.xor(ciph.doDec_ECB(tmp), suite.getAlteredIV(2)), ATTRIBUTE_VERSION);

		tmp = sm.read(CryptoCodes.STANDARD_BLOCKSIZE);
		ATTRIBUTE_DUMPLEN = ByteUtils.bytesToObject(Xor.xor(ciph.doDec_ECB(tmp), suite.getAlteredIV(4)), ATTRIBUTE_DUMPLEN);

		tmp = sm.read(CryptoCodes.STANDARD_BLOCKSIZE);
		ATTRIBUTE_DUMPCOUNT = ByteUtils.bytesToObject(Xor.xor(ciph.doDec_ECB(tmp), suite.getAlteredIV(6)), ATTRIBUTE_DUMPCOUNT);
	}
	
	
	public long getLen(){
		return ATTRIBUTE_DUMPLEN;
	}

	public long getCount(){
		return ATTRIBUTE_DUMPCOUNT;
	}
	
	public boolean isValid(){
		return ATTRIBUTE_VERSION == DUMPHEADER_VERSION;
	}
	
	private final int len = 3 * CryptoCodes.STANDARD_BLOCKSIZE;
	public int headerLength(){
		return len;
	}
}
