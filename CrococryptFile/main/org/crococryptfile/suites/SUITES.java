package org.crococryptfile.suites;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.crococryptfile.suites.capirsaaes.CAPIRSAAESMain;
import org.crococryptfile.suites.pbeaes.PBE1AESMain;
import org.crococryptfile.suites.pbecloakedaes2f.PBECloaked_AES2F_Main;
import org.crococryptfile.suites.pbecloakedaes2f.PBECloaked_AES2F_Main_1MB;
import org.crococryptfile.suites.pgpaes.PGPAESMain;
import org.crococryptfile.ui.resources._T;
import org.fhissen.utils.StreamMachine;
import org.fhissen.utils.os.OSDetector;
import org.fhissen.utils.os.OSDetector.OS;


public enum SUITES implements BasicFileinfo{
	PBE1_AES(BASENUMBER + 10),
	CAPI_RSAAES(BASENUMBER + 20),
	PGP_AES(BASENUMBER + 30),
	PBECLOAKED_AESTWO(BASENUMBER + 1000),
	PBECLOAKED1MB_AESTWO(BASENUMBER + 1001),
	
	;
	
	private int magicnumber;
	private ArrayList<OS> osspec = null;
	private SuitePARAM[] encparams, decparams;
	private SUITES(int magicnumber){
		this.magicnumber = magicnumber;
	}
	
	protected final void setOS(OS os){
		setOS(new OS[]{os});
	}

	protected final void setOS(OS[] oss){
		if(oss != null && oss.length > 0){
			osspec = new ArrayList<>();
			Collections.addAll(osspec, oss);
		}
	}
	
	protected final void setEncParams(SuitePARAM[] params){
		encparams = params;
	}
	
	protected final void setDecParams(SuitePARAM[] params){
		decparams = params;
	}
	
	public final SuitePARAM[] getEncryptParameters(){
		return encparams;
	}
	
	public final SuitePARAM[] getDecryptParameters(){
		return decparams;
	}
	
	public final boolean hasEncryptParameters(){
		return encparams != null && encparams.length > 0;
	}
	
	public final boolean hasDecryptParameters(){
		return decparams != null && decparams.length > 0;
	}
	
	public final int magicNumber(){
		return magicnumber;
	}
	
	public final boolean isAvailable(){
		if(osspec == null || osspec.contains(OSDetector.which())) return true;
		return false;
	}

	
	@SuppressWarnings("rawtypes")
	public static final ArrayList<Class> headers = new ArrayList<>();
	public static final ArrayList<Integer> numbers = new ArrayList<>();
	public static final ArrayList<String> descriptor = new ArrayList<>();
	public static final HashMap<Integer, SUITES> id = new HashMap<>();

	static{
		headers.add(PBE1AESMain.class);
		numbers.add(PBE1_AES.magicNumber());
		descriptor.add(_T.Suite_PBE1_AES.val());
		id.put(PBE1_AES.magicNumber(), PBE1_AES);

		headers.add(CAPIRSAAESMain.class);
		numbers.add(CAPI_RSAAES.magicNumber());
		descriptor.add(_T.Suite_CAPI_RSAAES.val());
		id.put(CAPI_RSAAES.magicNumber(), CAPI_RSAAES);

		headers.add(PGPAESMain.class);
		numbers.add(PGP_AES.magicNumber());
		descriptor.add(_T.Suite_PGP_AES.val());
		id.put(PGP_AES.magicNumber(), PGP_AES);

		headers.add(PBECloaked_AES2F_Main.class);
		numbers.add(PBECLOAKED_AESTWO.magicNumber());
		descriptor.add(_T.Suite_PBECLOAKED_AESTWO.val());
		id.put(PBECLOAKED_AESTWO.magicNumber(), PBECLOAKED_AESTWO);

		headers.add(PBECloaked_AES2F_Main_1MB.class);
		numbers.add(PBECLOAKED1MB_AESTWO.magicNumber());
		descriptor.add(_T.Suite_PBECLOAKED1MB_AESTWO.val());
		id.put(PBECLOAKED1MB_AESTWO.magicNumber(), PBECLOAKED1MB_AESTWO);

		
		PBE1_AES.setEncParams(new SuitePARAM[]{SuitePARAM.password});
		PBE1_AES.setDecParams(new SuitePARAM[]{SuitePARAM.password});
		
		CAPI_RSAAES.setOS(OS.WIN);
		CAPI_RSAAES.setEncParams(new SuitePARAM[]{SuitePARAM.capi_alias});
		
		PGP_AES.setEncParams(new SuitePARAM[]{SuitePARAM.pgp_enc});
		PGP_AES.setDecParams(new SuitePARAM[]{SuitePARAM.pgp_dec});
		
		PBECLOAKED_AESTWO.setEncParams(new SuitePARAM[]{SuitePARAM.password});
		PBECLOAKED_AESTWO.setDecParams(new SuitePARAM[]{SuitePARAM.password});

		PBECLOAKED1MB_AESTWO.setEncParams(new SuitePARAM[]{SuitePARAM.password});
		PBECLOAKED1MB_AESTWO.setDecParams(new SuitePARAM[]{SuitePARAM.password});
	}
	
	public static final int numberFromClass(Suite header){
		int no = headers.indexOf(header.getClass());
		return numbers.get(no);
	}

	@SuppressWarnings("rawtypes")
	public static final Class classFromSuites(SUITES suite){
		int no = numbers.indexOf(suite.magicNumber());
		return headers.get(no);
	}

	
	public static void create(OutputStream out, int magicnumber) throws IOException{
		StreamMachine.write(out, magicnumber);
	}
	
	public static SUITES read(File f){
		try {
			return readNclose(new FileInputStream(f));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static SUITES readNclose(InputStream is){
		SUITES suite = null;
		suite = read(is);

		try {
			is.close();
		} catch (Exception e) {}
		
		return suite;
	}

	public static SUITES read(InputStream is){
		int i = -1;
		i = StreamMachine.readO(is, i);
		return id.get(i);
	}
	
	private static String providerline;
	public static String providerListAsLine(){
		if(providerline == null){
			providerline = "";
			for(SUITES sx: SUITES.values())
				providerline += sx.name() + " ";
			providerline = providerline.trim();
		}
		return providerline;
	}
}
