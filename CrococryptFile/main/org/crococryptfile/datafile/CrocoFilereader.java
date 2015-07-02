package org.crococryptfile.datafile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.crococryptfile.CrococryptFile;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.pbecloakedaes2f.PBECloaked_AES2F_Main;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SUCCESS;
import org.fhissen.callbacks.SuccessCallback;
import org.fhissen.crypto.InitCrypto;
import org.fhissen.utils.StreamMachine;
import org.fhissen.utils.ui.StatusUpdate;


public class CrocoFilereader {
	static{
		InitCrypto.INIT();
	}
	
	private SuccessCallback cb;
	private Suite suite;
	private File croco;
	private File destdir;
	private StatusUpdate stat;
	
	public CrocoFilereader(SuccessCallback cb, Suite suite, File croco, File destdir){
		this.cb = cb;
		this.suite = suite;
		this.croco = croco;
		this.destdir = destdir;
	}
	
	public void execute(StatusUpdate status) {
		stat = status;
		if(croco.isDirectory() || !croco.exists()) return;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				SUCCESS success = SUCCESS.FALSE;

				try {
					if(suite == null) return;
					if(stat != null) stat.start();
					
					suite.setStatus(stat);

					FileInputStream is = new FileInputStream(croco);
					if(!(suite instanceof PBECloaked_AES2F_Main)) StreamMachine.read(is, SUITES.MAGICNUMBER_LENGTH);
					suite.readFrom(is);
					
					if(stat != null && !stat.isActive()){
						success = SUCCESS.CANCEL;
						return;
					}
					
					DumpHeader dh = new DumpHeader(suite);
					dh.readFrom(is);
					is.close();
					if(!dh.isValid()) return;
					
					if(stat != null) stat.receiveMessageSummary(_T.Decrypt_Start.msg(dh.ATTRIBUTE_DUMPCOUNT));
					DumpReader rf = new DumpReader(suite.suiteLength() + dh.headerLength());
					rf.setStatusReader(stat);
					rf.main(croco.getAbsolutePath(), destdir.getAbsolutePath(), suite, dh);
					
					if(stat == null || stat.isActive())
						success = SUCCESS.TRUE;
					if(stat != null && !stat.isActive())
						success = SUCCESS.CANCEL;
				}
				catch (FileNotFoundException e) {
					CrococryptFile.LASTERROR = _T.DecryptWindow_folderreadonly.val();
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				
				finally {
					if(cb != null) cb.callbackValue(this, success);
					if(stat != null) stat.finished();
					if(suite != null) suite.deinit();
				}
			}
		}).start();
	}
}
