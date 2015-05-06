package org.crococryptfile.suites.capirsaaes;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import org.fhissen.crypto.CryptoCodes;


public class CAPIRSAUtils {
	public static final String CAPI_KEYSTORE_USER = "Windows-MY";
	public static final String CAPI_PROVIDER = "SunMSCAPI";
	public static final String CAPI_RSACIPHER = "RSA/ECB/PKCS1Padding";
	
	public static class CAPIRSAAliases{
		public ArrayList<String> aliases = new ArrayList<>();
		public ArrayList<String> displaytexts = new ArrayList<>();
	}

	private static KeyStore keystore;
	static{
		try {
			keystore = KeyStore.getInstance(CAPI_KEYSTORE_USER, CAPI_PROVIDER);
			keystore.load(null,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final KeyStore getCAPI(){
		return keystore;
	}
	
	public static final CAPIRSAAliases getPrivates() {
		try {
			CAPIRSAAliases ret = new CAPIRSAAliases();
			
			Enumeration<String> en = keystore.aliases();
			while(en.hasMoreElements()){
				String alias = en.nextElement();
				PrivateKey key = (PrivateKey)keystore.getKey(alias, null);
				if(key == null || !key.getAlgorithm().equalsIgnoreCase(CryptoCodes.KEY_RSA)) continue;
				
				Certificate cert = keystore.getCertificate(alias);
				ret.aliases.add(alias);
				ret.displaytexts.add(((X509Certificate)cert).getSubjectDN() + " (" + alias + ", " + key.getAlgorithm() + ")");
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final CAPIRSAAliases getAll() {
		try {
			CAPIRSAAliases ret = new CAPIRSAAliases();
			
			Enumeration<String> en = keystore.aliases();
			while(en.hasMoreElements()){
				String alias = en.nextElement();
				
				Certificate cert = keystore.getCertificate(alias);
				if(!cert.getPublicKey().getAlgorithm().equalsIgnoreCase(CryptoCodes.KEY_RSA)) continue;
				
				ret.aliases.add(alias);
				ret.displaytexts.add(((X509Certificate)cert).getSubjectDN() + " (" + alias + ", " + CryptoCodes.KEY_RSA + ")");
			}
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static final String getDNFromAlias(String requested_alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException{
		Certificate cert = keystore.getCertificate(requested_alias);
		if(cert == null) return null;
		
		return ((X509Certificate)cert).getSubjectDN().toString();
	}

	public static final String getAliasFromDN(String requested_dn) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException{
		Enumeration<String> en = keystore.aliases();
		while(en.hasMoreElements()){
			String alias = en.nextElement();
			Certificate cert = keystore.getCertificate(alias);
			if(cert == null) continue;

			String tmp = ((X509Certificate)cert).getSubjectDN().toString();
			if(!requested_dn.equalsIgnoreCase(tmp)) continue;

			return alias;
		}
		
		return null;
	}
}
