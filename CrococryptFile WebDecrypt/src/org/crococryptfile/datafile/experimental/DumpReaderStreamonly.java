package org.crococryptfile.datafile.experimental;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;

import org.crococryptfile.datafile.DumpHeader;
import org.crococryptfile.datafile.IndexEntry;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.ui.resources._T;
import org.fhissen.utils.Codes;
import org.fhissen.utils.StreamMachine.StreamResult;
import org.fhissen.utils.ui.StatusUpdate;

public class DumpReaderStreamonly {
	private StatusUpdate status;
	private long max_len = 0;
	private long offset = -1;
	private ItemZipper dest = null;
	private CountingBAInputStream dump = null;

	private final byte[] buffer = new byte[1024 * 1024];

	public DumpReaderStreamonly(long offset){
		this.offset = offset;
	}

	public void setStatusReader(StatusUpdate status){
		this.status = status;
	}

	public void main(CountingBAInputStream src, ItemZipper dst, Suite dfile, DumpHeader dh) throws IOException, FileNotFoundException{
		initFiles(src, dst);
		read(dfile, dh);
	}
	
	private void read(Suite dfile, DumpHeader dh) throws IOException, FileNotFoundException{
		dest.start();
		
		
		InputStream idxis = readSealed(dfile, dh);
		CountingBAInputStream is = dump.clone();
		
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
			

			OutputStream singleout = dest.newItem(new String(entry.ATTRIBUTE_NAME, Codes.UTF8), len, last);

			if(len == 0){
				if(status != null) status.receiveProgress(((int)(((float)(no+len)/(float)max_len) * 100f)));
				dest.closeLast();
				continue;
			}
			
			is.newItem(no, len);

			InputStream in = new InflaterInputStream(dfile.getCipher().createIS_CBC_Pad(is, entry.ATTRIBUTE_IV));
			
			int l = in.read(buffer);
			while(l >= 0 && active()){
				singleout.write(buffer, 0, l);
				l = in.read(buffer);
			}
			

			dest.closeLast();
			
			if(!active()) {
				dest.cancelLast();
			}
			
			if(status != null) status.receiveProgress(((int)(((float)(no+len)/(float)max_len) * 100f)));
		}
		
		idxis.close();
		is.realClose();
		dest.close();
		
		if(status != null){
			status.receiveProgress(100);
			status.receiveMessageSummary(_T.General_done.val());
		}
		
		if(se == StreamResult.ReadException) throw new IOException("error while reading");
	}

	private void initFiles(CountingBAInputStream srcdump, ItemZipper dest) throws IOException{
		this.dest = dest;

		dump = srcdump;
		if(status != null) max_len = dump.dumpLength();
	}
	
	private InputStream readSealed(Suite dfile, DumpHeader dh) {
			dump.newItem(dh.ATTRIBUTE_DUMPLEN, dump.dumpLength() - dh.ATTRIBUTE_DUMPLEN);
			InputStream in = new InflaterInputStream(dfile.getCipher().createIS_CBC_Pad(dump.getIS(), dfile.getAlteredIV(10)));
			return in;
	}
	
	private boolean active(){
		if(status == null) return true;
		return status.isActive();
	}
}
