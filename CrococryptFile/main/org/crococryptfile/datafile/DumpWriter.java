package org.crococryptfile.datafile;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.crococryptfile.streams.CountingOutputStream;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.ui.resources._T;
import org.fhissen.crypto.CryptoUtils;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.os.OSDetector;
import org.fhissen.utils.ui.StatusUpdate;


public class DumpWriter {
	private CountingOutputStream mainstream;
	private Suite icroc;
	private Index index;
	private File outputfile = null;

	private StatusUpdate stat;

	private final byte[] buffer = new byte[1024 * 1024];


	public DumpWriter(Suite croco, Index index){
		this.icroc = croco;
		this.index = index;
	}
	
	public void setStatusReader(StatusUpdate status){
		stat = status;
	}
	
	public void setOutfile(File file){
		outputfile = file;
	}
	
	public void initStreams(OutputStream os) {
		mainstream = new CountingOutputStream(os);
	}

	public void close() throws IOException{
		close(true);
	}

	public void close(boolean c_deinit) throws IOException{
		mainstream.flush();
		mainstream.realClose();
	}
	
	public void seal(final boolean c_deinit, File f) throws IOException{
		OutputStream os = new DeflaterOutputStream(icroc.getCipher().createOS_CBC_Pad(new FileOutputStream(f, true), icroc.getAlteredIV(10)),
				new Deflater(Deflater.BEST_SPEED), true);

		index.write(os);
		
		os.flush();
		os.close();
	}
	
	public void write(String dir) throws IOException{
		File src;
		src = new File(dir);
		
		if(src.isDirectory()){
			final int base = src.getParent().length();
			src.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if(!active()) return false;
					
					try {
						if(f.isDirectory()){
							f.listFiles(this);
						}
						else{
							addFile(f, base);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return false;
				}
			});
		}
		else{
			if(src.isFile()){
				addFile(src, src.getParent().length());
			}
		}
	}

	private void addFile(File f, int base) throws IOException{
		if(outputfile != null && outputfile.equals(f)) return;
		
		String sa = saniPath(f.getAbsolutePath(), base);
		
		if(stat != null){
			stat.receiveMessageDetails(_T.CrocoFilewriter_encrypting.msg(FileUtils.shortenDisplayFile(f)));
			stat.receiveDetailsProgress(0);
		}

		mainstream.newItem();
		long start = mainstream.getNumber();
		
		byte[] iv = CryptoUtils.randIv16();
		
		final long filelen = f.length();
		if(filelen == 0){
			index.idxEntry(sa, start, 0, f.lastModified(), iv);
			return;
		}
		
		FileInputStream fis = null;
		int len = -1;
		try {
			fis = new FileInputStream(f);
			len = fis.read(buffer);
		} catch (Exception e) {
			System.out.println("Error while reading " + f.getAbsolutePath());
			try {
				if(fis!=null) fis.close();
			} catch (Exception e2) {}
		}
		
		int deflater = Deflater.BEST_SPEED;
		if(checkExt(f.getName())) deflater = Deflater.NO_COMPRESSION;
		
		OutputStream os = new DeflaterOutputStream(icroc.getCipher().createOS_CBC_Pad(mainstream, iv),
					new Deflater(deflater), true);
		
		long writtenlen = 0;
		while(len >= 0 && active()){
			os.write(buffer, 0, len);
			writtenlen += len;
			if(stat != null) stat.receiveDetailsProgress((int) (((float)writtenlen / (float)filelen) * 100f));
			len = fis.read(buffer);
		}
		os.flush();
		os.close();
		fis.close();

		if(!active()) return;
		
		index.idxEntry(sa, start, mainstream.getLength(), f.lastModified(), iv);
	}
	
	
	private boolean active(){
		if(stat == null) return true;
		return stat.isActive();
	}

	
	private final String saniPath(String tmp, int baselen){
		if(OSDetector.isWin()){
			return tmp.substring(baselen, tmp.length()).replace('\\', '/');
		}
		return tmp.substring(baselen, tmp.length());
	}
	
	
	private static final String[] nocompress = new String[]{
		"jpg",
		"jpeg",
		"png",
		"gif",
		"zip",
		"gz",
		"cab",
		"bzip",
		"bzip2",
		"bz2",
		"apk",
		"jar",
		"mpeg",
		"mpg",
		"mp4",
		"mp3",
		"ogg",
		"mp2",
		"m2v",
		"xz",
		"gz",
		"7z",
	};
	
	static{
		for(int i=0; i<nocompress.length; i++){
			if(!nocompress[i].startsWith(".")) nocompress[i] = "." + nocompress[i];
			nocompress[i] = nocompress[i].toLowerCase();
		}
	}

	private static final boolean checkExt(String name){
		for(String s: nocompress)
			if(name.toLowerCase().endsWith(s)) return true;
		return false;
	}
}
