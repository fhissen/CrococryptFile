package org.crococryptfile.suites.pgpaes;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.crococryptfile.CrococryptFile;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.SuiteMODE;
import org.crococryptfile.suites.SuitePARAM;
import org.crococryptfile.suites.pgpaes.PGPUtils.PGPSecretKeyFinder;
import org.crococryptfile.ui.resources._T;
import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoCodes.BASECIPHER;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.crypto.FSecretKeySpec;
import org.fhissen.crypto.KeygenUtils;
import org.fhissen.utils.ByteUtils;
import org.fhissen.utils.StreamMachine;


public class PGPAESMain extends Suite {
	private int ATTRIBUTE_HEADCOUNT;
	
	private ArrayList<PGPAESMain_Header> headers = new ArrayList<>();
	private class PGPAESMain_Header{
		private long ATTRIBUTE_KEYID; 
		private int ATTRIBUTE_KEYLEN; 
		private byte[] ATTRIBUTE_KEY; 
	}
	
	private byte[] ATTRIBUTE_IV = new byte[CryptoCodes.STANDARD_IVSIZE]; 
	
	private byte[] aesplain;
	private CipherMain ciph;
	private String keyfile;
	private char[] pass;
	private ArrayList<Long> kids = new ArrayList<>();

	public PGPAESMain(){
		super(SUITES.PGP_AES);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void _init(SuiteMODE mode, HashMap<SuitePARAM, Object> params) throws IllegalArgumentException{
		if(mode == SuiteMODE.ENCRYPT){
			if(params == null || params.size() < 2)  throw new IllegalArgumentException("PGPEncrypt: not enough params specified");
			ArrayList<?> arr = (ArrayList<?>) params.get(SuitePARAM.pgp_keyidlist);
			if(arr.size() == 0) throw new IllegalArgumentException("PGPEncrypt: no keyid specified");
			if(!(arr.get(0) instanceof Long)) throw new IllegalArgumentException("PGPEncrypt: internal keyid error");
			
			kids = (ArrayList<Long>) arr;

			aesplain = new KeygenUtils().raw(CryptoCodes.AES_KEYSIZE_BITS);
			ciph = new CipherMain(BASECIPHER.AES, new FSecretKeySpec(aesplain));
			keyfile = (String) params.get(SuitePARAM.pgp_pubkeyfile);
			ATTRIBUTE_IV = CryptoUtils.randIv16();

			for(int i=0; i<kids.size(); i++){
				PGPAESMain_Header head = new PGPAESMain_Header();
				
				head.ATTRIBUTE_KEYID = kids.get(i);			
				if(head.ATTRIBUTE_KEYID == 0) throw new IllegalArgumentException("PGPEncrypt mode must specify a valid KeyID");
				
				try {
					head.ATTRIBUTE_KEY = enc(aesplain, head);
					head.ATTRIBUTE_KEYLEN = head.ATTRIBUTE_KEY.length;
				} catch (Exception e) {
					if(head.ATTRIBUTE_KEY == null) throw new IllegalArgumentException("PGPEncryption failed (wrong keyid?)");
					throw new IllegalArgumentException("PGPEncryption failed (wrong credentials?): " + e.getLocalizedMessage());
				}
				
				headers.add(head);
			}
		}
		else if(mode == SuiteMODE.DECRYPT){
			keyfile = (String) params.get(SuitePARAM.pgp_privkeyfile);
			pass = (char[]) params.get(SuitePARAM.password);
		}
	}
	
	private byte[] enc(byte[] plainaes, PGPAESMain_Header head) throws Exception{
	    return PGPUtils.encrypt(new File(keyfile), head.ATTRIBUTE_KEYID, plainaes);
	}
	
	private byte[] dec(byte[] encaes, PGPAESMain_Header head) throws Exception{
	    return PGPUtils.decrypt(new File(keyfile), head.ATTRIBUTE_KEYID, pass, encaes);
	}
	
	@Override
	protected void _writeTo(OutputStream out) throws IllegalStateException{
		try {
			ATTRIBUTE_HEADCOUNT = headers.size();
			StreamMachine.write(out, ATTRIBUTE_HEADCOUNT);

			for(PGPAESMain_Header head: headers){
				StreamMachine.write(out,
						head.ATTRIBUTE_KEYID,
						head.ATTRIBUTE_KEYLEN,
						head.ATTRIBUTE_KEY
						);
			}
			
			StreamMachine.write(out, ciph.doEnc_ECB(ATTRIBUTE_IV));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void _readFrom(InputStream is) throws IllegalStateException{
		StreamMachine sm = new StreamMachine(is);
		
		ATTRIBUTE_HEADCOUNT = sm.readO(ATTRIBUTE_HEADCOUNT);
		
		PGPSecretKeyFinder finder = new PGPSecretKeyFinder(keyfile);
		boolean found = false;

		for(int i=0; i<ATTRIBUTE_HEADCOUNT; i++){
			PGPAESMain_Header head = new PGPAESMain_Header();
			head.ATTRIBUTE_KEYID = sm.readO(head.ATTRIBUTE_KEYID);
			head.ATTRIBUTE_KEYLEN = sm.readO(head.ATTRIBUTE_KEYLEN);
			if(head.ATTRIBUTE_KEYLEN > 1024 * 1024 * 10) throw new IllegalStateException("error while parsing encrypted key");
			head.ATTRIBUTE_KEY = new byte[head.ATTRIBUTE_KEYLEN];
			sm.read(head.ATTRIBUTE_KEY);

			try {
				if(aesplain == null && finder.exists(head.ATTRIBUTE_KEYID)){
					if(!found) found = true;

					aesplain = dec(head.ATTRIBUTE_KEY, head);
					if(aesplain != null) ciph = new CipherMain(BASECIPHER.AES, new FSecretKeySpec(aesplain));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			headers.add(head);
		}
		
		finder.close();
		
		if(!found){
			CrococryptFile.LASTERROR = _T.PGP_errorNokey.val();
			throw new IllegalStateException("no suitable PGP secret key found");
		}
		
		if(aesplain == null){
			CrococryptFile.LASTERROR = _T.PGP_errorSeckeyfailed.val();
			throw new IllegalStateException("key could not be decrypted, wrong credentials?");
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
		int size = ATTRIBUTE_IV.length;
		size += ByteUtils.sizeInBytes(ATTRIBUTE_HEADCOUNT);
		
		for(PGPAESMain_Header head: headers){
			size += ByteUtils.sizeInBytes(head.ATTRIBUTE_KEYID, head.ATTRIBUTE_KEYLEN, head.ATTRIBUTE_KEY);
		}
		
		return size;
	}
	
	@Override
	public byte[] getAttributeIV(){
		return ATTRIBUTE_IV;
	}
	
	@Override
	public final void deinit(){
		CryptoUtils.kill(aesplain, ATTRIBUTE_IV);
		CryptoUtils.kill(pass);
		if(ciph != null) ciph.deinint();
		System.gc();
	}
	
	@Override
	protected void finalize() throws Throwable {
		deinit();
		super.finalize();
	}
}
