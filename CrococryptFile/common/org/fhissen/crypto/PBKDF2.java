package org.fhissen.crypto;

/*
 * This class is based on the Bouncy Castle ((c) http://www.bouncycastle.org/) Java class org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
 * See http://www.bouncycastle.org/ for the license of BC
 */

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class PBKDF2 {
	private Mac hMac;
	private byte[] state;
	private FSecretKeySpec key;

	public PBKDF2(Mac mac) {
		hMac = mac;
		state = new byte[hMac.getMacLength()];
	}

	private void F(byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
		if (S != null) {
			hMac.update(S, 0, S.length);
		}

		try {
			hMac.update(iBuf, 0, iBuf.length);
			hMac.doFinal(state, 0);

			System.arraycopy(state, 0, out, outOff, state.length);

			for (int count = 1; count < c; count++) {
				hMac.update(state, 0, state.length);
				hMac.doFinal(state, 0);

				for (int j = 0; j != state.length; j++) {
					out[outOff + j] ^= state[j];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] generateDerivedKey(int dkLen, byte[] password, byte[] salt, int iterationCount) {
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

				F(salt, iterationCount, iBuf, outBytes, outPos);
				outPos += hLen;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outBytes;
	}

	public byte[] generateDerivedParameters(int keySize, byte[] password, byte[] salt, int iterationCount) throws IllegalStateException {
		keySize = keySize / 8;

		byte[] dKey = generateDerivedKey(keySize, password, salt, iterationCount);
		
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

		return dKey;
	}
}
