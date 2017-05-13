package org.crococryptfile.suites;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.crococryptfile.suites.pbecloakedaes2f.PBECloaked_AES2F_Main;
import org.fhissen.crypto.CipherMain;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.utils.ui.StatusUpdate;


public abstract class Suite {
	private SuiteMODE mode;
	private StatusUpdate status;
	private boolean initialized = false;
	
	protected Suite(){}
	
	public final void init(SuiteMODE mode, HashMap<SuitePARAM, Object> params, StatusUpdate status) throws IllegalArgumentException{
		if(mode == null) throw new IllegalArgumentException("no mode specified");
		
		this.status = status;
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
		if(len < 0){
			len = headerLength();
			if(!(this instanceof PBECloaked_AES2F_Main)) len += SUITES.MAGICNUMBER_LENGTH;
		}
		return len;
	}
	
	public final StatusUpdate getStatus(){
		return status;
	}
	
	
	public static final Suite getInstance(SUITES suite){
		try {
			return (Suite)(SUITES.classFromSuites(suite).newInstance());
		} catch (Exception e) {
			System.err.println("getInstance: " + e.getLocalizedMessage());
			return null;	
		}
	}

	public static final Suite getInitializedInstance(SUITES suite, SuiteMODE mode, HashMap<SuitePARAM, Object> params, StatusUpdate status){
		try {
			Suite instance = (Suite)(SUITES.classFromSuites(suite).newInstance());
			instance.init(mode, params, status);
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("getInitializedInstance: " + e.getLocalizedMessage());
			return null;	
		}
	}
	
	
	public static final void getInitializedInstanceAsync(final SUITES suite, final SuiteMODE mode,
			final HashMap<SuitePARAM, Object> params, final StatusUpdate status, final SuiteReceiver rec){
		if(rec == null){
			System.err.println("SuiteReceiver == null");
			return;
		}

		if(suite == null){
			System.err.println("Requested SUITES == null");
			rec.receiveInitializedInstance(null);
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Suite instance = (Suite)(SUITES.classFromSuites(suite).newInstance());
					instance.init(mode, params, status);
					rec.receiveInitializedInstance(instance);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("getInitializedInstanceAsync: " + e.getLocalizedMessage());
					rec.receiveInitializedInstance(null);
				}
			}
		}).start();
	}
	
	
	public static final Suite getInitializedInstance(SUITES suite, SuiteMODE mode, SuitePARAM aparam, Object avalue){
		HashMap<SuitePARAM, Object> params = new HashMap<>();
		params.put(aparam, avalue);
		return getInitializedInstance(suite, mode, params, null);
	}
	

	public interface SuiteReceiver{
		public void receiveInitializedInstance(Suite suite);
	}
}
