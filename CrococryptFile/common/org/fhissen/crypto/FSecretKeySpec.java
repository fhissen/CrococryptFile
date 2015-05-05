package org.fhissen.crypto;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;


public class FSecretKeySpec extends SecretKeySpec{
	private static final long serialVersionUID = 1L;

	private byte[] k;
	private String alg;
	private ArrayList<byte[]> all = new ArrayList<>();
	
	public FSecretKeySpec(byte[] key, String algorithm) {
		super(CryptoUtils.randIv(key.length), algorithm);
		
		k = key;
		alg = algorithm;
	}
	
	public FSecretKeySpec(byte[] key) {
		this(key, CryptoCodes.KEY_AES);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String getAlgorithm() {
		return alg;
	}

	@Override
	public byte[] getEncoded() {
		byte[] next = Arrays.copyOf(k, k.length);
		all.add(next);
		return next;
	}

	@Override
	public String getFormat() {
		return "RAW";
	}
	
	public void wipe(boolean key_inclusive){
		for(byte[] b: all)
			CryptoUtils.kill(b);
		all.clear();
		
		if(key_inclusive) CryptoUtils.kill(k);
	}

	public static final FSecretKeySpec make(byte[] key){
		return new FSecretKeySpec(key, CryptoCodes.KEY_AES);
	}
}
