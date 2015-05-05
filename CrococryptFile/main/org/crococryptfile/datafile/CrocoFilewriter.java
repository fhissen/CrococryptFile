package org.crococryptfile.datafile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.crococryptfile.CrococryptFile;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SUCCESS;
import org.fhissen.callbacks.SuccessCallback;
import org.fhissen.crypto.InitCrypto;
import org.fhissen.utils.ui.StatusUpdate;

public class CrocoFilewriter {
	static{
		InitCrypto.INIT();
	}
	
	private SuccessCallback cb;
	private Suite suite;
	private File[] files;
	private File outfile;
	private StatusUpdate stat;

	public CrocoFilewriter(SuccessCallback cb, Suite suite, File[] files, File outfile){
		this.cb = cb;
		this.suite = suite;
		this.files = files;
		this.outfile = outfile;
	}
	
	public void execute() {
		execute(null);
	}

	public void execute(StatusUpdate status) {
		this.stat = status;

		new Thread(new Runnable() {
			@Override
			public void run() {
				SUCCESS success = SUCCESS.FALSE;
				Index index = null;
				OutputStream os = null;
				
				try {
					if(suite == null) return;
					if(outfile.isDirectory()) return;
					
					if(stat != null) stat.start();
					
					index = new Index(new File(outfile.getParentFile(), outfile.getName() + ".parttmp"));
					DumpWriter wf = new DumpWriter(suite, index);
					wf.setStatusReader(stat);
					wf.setOutfile(outfile);
					
					os = new FileOutputStream(outfile);
					SUITES.create(os, SUITES.numberFromClass(suite));
					suite.writeTo(os);
					DumpHeader dh = new DumpHeader(suite);
					dh.createOut(os);
					
					wf.initStreams(os);

					int len = files.length;
					int i=0;
					
					for(File tmp: files){
						if(!tmp.exists()) continue;
						
						wf.write(tmp.getAbsolutePath());
						if(stat != null) {
							if(!stat.isActive()) break;
							i++;
							stat.receiveProgress((int)(((float)i/(float)len) * 100f));
						}
					}
					wf.close(false);
					
					if(stat != null){
						if(stat.isActive()){
							stat.receiveDetailsProgress(100);
							stat.receiveMessageSummary(_T.General_done.val());
						}
						else{
							success = SUCCESS.CANCEL;
							outfile.delete();
							index.delete();
							return;
						}
					}
				
					long dumplen = outfile.length();
					long ct = index.getCount();
					
					wf.seal(false, outfile);
					
					RandomAccessFile raf = new RandomAccessFile(outfile, "rw");
					raf.seek(suite.suiteLength());
					FileOutputStream fos = new FileOutputStream(raf.getFD());
					dh.ATTRIBUTE_DUMPLEN = dumplen;
					dh.ATTRIBUTE_DUMPCOUNT = ct;
					dh.createOut(fos);
					fos.close();
					raf.close();
					
					success = SUCCESS.TRUE;
				}
				catch (FileNotFoundException e) {
					CrococryptFile.LASTERROR = _T.EncryptWindow_folderreadonly.val();
					e.printStackTrace();
				}
				catch (IOException e) {
					CrococryptFile.LASTERROR = _T.EncryptWindow_folderreadonly.val();
					e.printStackTrace();
				}
				finally{
					if(success != SUCCESS.TRUE){
						try {
							index.delete();
							os.close();
						} catch (Exception e2) {}
						outfile.delete();
					}
					
					if(cb != null) cb.callbackValue(this, success);
					if(stat != null) stat.finished();
					if(suite != null) suite.deinit();
				}
			}
		}).start();
	}
}
