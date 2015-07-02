package org.crococryptfile.suites.pbecloakedaes2f;

/*
 * This class is based on the Bouncy Castle ((c) http://www.bouncycastle.org/) Java class org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
 * See http://www.bouncycastle.org/ for the license of BC
 */

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.crococryptfile.ui.resources._T;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.crypto.FSecretKeySpec;
import org.fhissen.utils.ui.StatusUpdate;


public class PBKDF2Interstep {
	public static abstract class InterstepChecker{
		public abstract boolean found(byte[] intermediate_key);
	}
	
	private Mac hMac;
	private byte[] state;
	private FSecretKeySpec key;
	private InterstepChecker checker;
	private StatusUpdate status;

	public PBKDF2Interstep(Mac mac, InterstepChecker checker, StatusUpdate status) {
		hMac = mac;
		state = new byte[hMac.getMacLength()];
		this.checker = checker;
		this.status = status;
	}

	private void F(byte[] S, byte[] iBuf, byte[] out, int outOff) {
		if (S != null) {
			hMac.update(S, 0, S.length);
		}
		
		long l = System.currentTimeMillis();
		long lcomp = l;

		try {
			hMac.update(iBuf, 0, iBuf.length);
			hMac.doFinal(state, 0);

			System.arraycopy(state, 0, out, outOff, state.length);

			for (long count = 1; count < Long.MAX_VALUE && (status == null || status.isActive()); count++) {
				hMac.update(state, 0, state.length);
				hMac.doFinal(state, 0);

				for (int j = 0; j != state.length; j++) {
					out[outOff + j] ^= state[j];
				}
				
				if(status != null){
					lcomp = System.currentTimeMillis();
					if(lcomp - l > 10 * 1000){
						status.receiveMessageDetails(_T.PBKDF2Interstep_steplong.val());
						l = lcomp;
					}
				}
				
				
				if(checker.found(out)) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] generateDerivedKey(int dkLen, byte[] password, byte[] salt) {
		int hLen = hMac.getMacLength();
		int l = (dkLen + hLen - 1) / hLen;
		byte[] iBuf = new byte[4];
		byte[] outBytes = new byte[l * hLen];
		int outPos = 0;

		try {
			key = new FSecretKeySpec(password, "AES");
			hMac.init(key);

			for (int i = 1; i <= l; i++) {
				int pos = 3;
				while (++iBuf[pos] == 0) {
					--pos;
				}

				F(salt, iBuf, outBytes, outPos);
				outPos += hLen;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outBytes;
	}

	public byte[] generateDerivedParameters(int keySize, byte[] password, byte[] salt) throws IllegalStateException {
		keySize = keySize / 8;

		if(status != null) status.receiveMessageDetails(_T.PBKDF2Interstep_start.val());
		
		byte[] dKey = generateDerivedKey(keySize, password, salt);
		
		if(status != null && status.isActive()) status.receiveMessageDetails(_T.PBKDF2Interstep_found.val());
		
		try {
			key.wipe(true);
			byte[] dummy = new byte[password.length];
			CryptoUtils.kill(dummy);
			hMac.init(new SecretKeySpec(dummy, "AES"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(dKey.length < keySize)
			throw new IllegalStateException("General failure: Generated array not in keysize!");

		if (dKey.length > keySize){
			byte[] dk2 = new byte[keySize];
			System.arraycopy(dKey, 1, dk2, 0, keySize);
			CryptoUtils.kill(dKey);
			return dk2;
		}

		System.gc();

		return dKey;
	}
}
