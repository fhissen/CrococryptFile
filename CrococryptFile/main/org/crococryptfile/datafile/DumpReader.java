package org.crococryptfile.datafile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.crococryptfile.streams.CountingRAInputStream;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.pbecloakedaes2f.PBECloaked_AES2F_Main;
import org.crococryptfile.ui.resources._T;
import org.fhissen.utils.Codes;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.StreamMachine.StreamResult;
import org.fhissen.utils.ui.StatusUpdate;


public class DumpReader {
	private StatusUpdate status;
	private long max_len = 0;
	private long offset = -1;
	private File dest = null;
	private File dump = null;

	private final byte[] buffer = new byte[1024 * 1024];

	public DumpReader(long offset){
		this.offset = offset;
	}

	public void setStatusReader(StatusUpdate status){
		this.status = status;
	}

	public void main(String src, String dst, Suite dfile, DumpHeader dh) throws IOException, FileNotFoundException{
		initFiles(src, dst);
		read(dfile, dh);
	}
	
	private void read(Suite dfile, DumpHeader dh) throws IOException, FileNotFoundException{
		if(!dump.exists() || !dump.isFile()) return;

		dest.mkdirs();
		if(!dest.exists() || !dest.isDirectory()){
			throw new FileNotFoundException("Destination directory could not be created");
		}
		
		String canonicalDestpath = dest.getCanonicalPath();

		InputStream idxis = readSealed(dfile, dh);
		CountingRAInputStream is = new CountingRAInputStream((dump));
		
		StreamResult se = null;
		
		while(true) {
			if(!active()) break;
			
			IndexEntry entry = null; 
			try {
				entry = IndexEntry.readFrom(idxis);
			} catch (Exception e) {
				se = StreamResult.ReadException;
				break;
			}
			
			if(entry == null) break;

			long no = entry.ATTRIBUTE_OFFSET;
			long len = entry.ATTRIBUTE_SIZE;
			long last = entry.ATTRIBUTE_MODIFIED;
			

			no += offset;
			
			File ftmp = new File(dest, new String(entry.ATTRIBUTE_NAME, Codes.UTF8));
			
			File securityCheck = null;
			try {
				securityCheck = ftmp.getCanonicalFile();
				if(!securityCheck.getAbsolutePath().startsWith(canonicalDestpath)) securityCheck = null;
			} catch (Exception e) {}
			if(securityCheck == null) continue;
			
			ftmp.getParentFile().mkdirs();


			if(status != null) status.receiveMessageDetails(_T.CrocoFilereader_decrypting.msg(FileUtils.shortenDisplayFile(ftmp)));
			
			if(len == 0){
				ftmp.createNewFile();
				if(last > 0) ftmp.setLastModified(last);
				if(status != null) status.receiveProgress(((int)(((float)(no+len)/(float)max_len) * 100f)));
				continue;
			}
			
			
			is.newItem(no, len);
			FileOutputStream fos = new FileOutputStream(ftmp);
			
			InputStream in = new InflaterInputStream(
						dfile.getCipher().createIS_CBC_Pad(is, entry.ATTRIBUTE_IV), new Inflater(), Codes.ZIP_BUFFERSIZE
					);
			
			int l = in.read(buffer);
			while(l >= 0 && active()){
				fos.write(buffer, 0, l);
				l = in.read(buffer);
			}
			
			fos.flush();
			fos.close();
			if(last > 0) ftmp.setLastModified(last);
			
			if(!active()) ftmp.delete();
			
			if(status != null) status.receiveProgress(((int)(((float)(no+len)/(float)max_len) * 100f)));
		}
		
		try {
			idxis.close();
		} catch (Exception e) {}
		try {
			is.realClose();
		} catch (Exception e) {}
		
		if(status != null){
			status.receiveProgress(100);
			status.receiveMessageSummary(_T.General_done.val());
		}
		
		if(dfile instanceof PBECloaked_AES2F_Main) return;
		
		if(se == StreamResult.ReadException) throw new IOException("error while reading");
	}

	private void initFiles(String srcdump, String destdir) throws IOException{
		dest = new File(destdir);
		dest.mkdirs();

		dump = new File(srcdump);
		if(status != null) max_len = dump.length();
	}
	
	private InputStream readSealed(Suite dfile, DumpHeader dh) {
			CountingRAInputStream fis = new CountingRAInputStream(dump, true);
			fis.newItem(dh.ATTRIBUTE_DUMPLEN, dump.length() - dh.ATTRIBUTE_DUMPLEN);
			
			InputStream in = new InflaterInputStream(dfile.getCipher().createIS_CBC_Pad(fis, dfile.getAlteredIV(10)), new Inflater(), Codes.ZIP_BUFFERSIZE);
			return in;
	}
	
	private boolean active(){
		if(status == null) return true;
		return status.isActive();
	}
}
