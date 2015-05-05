package org.crococryptfile.suites;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;


public abstract class Suite {
	private SUITES suite;
	private SuiteMODE mode;
	private boolean initialized = false;
	
	protected Suite(SUITES suite){
		this.suite = suite;
	}
	
	public SUITES suite(){
		return suite;
	}
	
	public final void init(SuiteMODE mode, HashMap<SuitePARAM, Object> params) throws IllegalArgumentException{
		if(mode == null) throw new IllegalArgumentException("no mode specified");
		
		_init(mode, params);
		
		this.mode = mode;
		initialized = true;
	}
	
	public SuiteMODE getMode(){
		return mode;
	}
	
	public final void writeTo(OutputStream out) throws IllegalStateException{
		if(!initialized) throw new IllegalStateException("not initialized");
		if(getMode() != SuiteMODE.ENCRYPT) throw new IllegalStateException("wrong mode");
		
		_writeTo(out);
	}
	
	public final void readFrom(InputStream is) throws IllegalStateException{
		if(!initialized) throw new IllegalStateException("not initialized");
		if(getMode() != SuiteMODE.DECRYPT) throw new IllegalStateException("wrong mode");
		
		_readFrom(is);
	}

	abstract protected void _init(SuiteMODE mode, HashMap<SuitePARAM, Object> params) throws IllegalArgumentException;
	abstract protected void _writeTo(OutputStream out) throws IllegalStateException;
	abstract protected void _readFrom(InputStream is) throws IllegalStateException;
	abstract public CipherMain getCipher();
	abstract public byte[] getAttributeIV();
	abstract public int headerLength();
	abstract public void deinit();
	
	public final byte[] getAlteredIV(int add){
		add = add % CryptoCodes.STANDARD_IVSIZE;
		byte[] buf = Arrays.copyOf(getAttributeIV(), CryptoCodes.STANDARD_IVSIZE);
		buf[add]++;
		return buf;
	}
	
	private int len = -1;
	public final int suiteLength(){
		if(len < 0)
			len = headerLength() + SUITES.MAGICNUMBER_LENGTH;
		return len;
	}
	
	
	public static final Suite getInstance(SUITES suite){
		try {
			return (Suite)(SUITES.classFromSuites(suite).newInstance());
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;	
		}
	}

	public static final Suite getInitializedInstance(SUITES suite, SuiteMODE mode, HashMap<SuitePARAM, Object> params){
		try {
			Suite instance = (Suite)(SUITES.classFromSuites(suite).newInstance());
			instance.init(mode, params);
			return instance;
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;	
		}
	}
	
	public static final Suite getInitializedInstance(SUITES suite, SuiteMODE mode, SuitePARAM aparam, Object avalue){
		HashMap<SuitePARAM, Object> params = new HashMap<>();
		params.put(aparam, avalue);
		return getInitializedInstance(suite, mode, params);
	}
	
	public static final Suite getInitializedInstance(SUITES suite, SuiteMODE mode){
		return getInitializedInstance(suite, mode, null);
	}
}
