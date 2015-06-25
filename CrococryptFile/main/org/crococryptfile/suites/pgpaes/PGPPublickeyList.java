package org.crococryptfile.suites.pgpaes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.fhissen.utils.ByteUtils;


public class PGPPublickeyList {
	public static class PGPPublickeyListItem{
		public String show;
		public Long id;
	}
	
	public static final ArrayList<PGPPublickeyListItem> items = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	public static void main(File pubkeyfile) throws IOException, PGPException{
		PGPPublickeyList.items.clear();

		if(pubkeyfile.exists() && pubkeyfile.isFile() && pubkeyfile.length() > 0){
			InputStream input = new FileInputStream(pubkeyfile);
	        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input));
	        
            Iterator keyRingIter = pgpPub.getKeyRings();
            while (keyRingIter.hasNext()) {
                PGPPublicKeyRing keyRing = (PGPPublicKeyRing)keyRingIter.next();
                if(keyRing != null){
                	Iterator it = ((PGPPublicKey)keyRing.getPublicKeys().next()).getUserIDs();
                	while(it.hasNext()){
                		String username = it.next().toString();
                		Iterator it2 = keyRing.getPublicKeys();
                		int i=0;
                		PGPPublicKey key = null;
                		while(it2.hasNext()){
                			Object tmpkey = it2.next();
                			i++;
                			if(i == 2){
                				key = (PGPPublicKey) tmpkey;
                				break;
                			}
                		}
                		
                		if(i == 2 && key != null && !key.isMasterKey() && key.isEncryptionKey()){
                			PGPPublickeyListItem item = new PGPPublickeyListItem();
	                		item.show = username + " ("  + Hex.encodeHexString(ByteUtils.longToBytes(keyRing.getPublicKey().getKeyID())).substring(8).toUpperCase() + ")";
	                		item.id = key.getKeyID();
	                		items.add(item);
                		}
                		else{
                			System.err.println("Unknown format error while parsing PGP keyring");
                		}
                	}
                }
            }
            
            Collections.sort(items, new Comparator<PGPPublickeyListItem>() {
				@Override
				public int compare(PGPPublickeyListItem o1, PGPPublickeyListItem o2) {
					return o1.show.compareTo(o2.show);
				}
			});

            input.close();
		}
	}
}
