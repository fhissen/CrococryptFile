package org.crocowebdec.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.crococryptfile.datafile.DumpHeader;
import org.crococryptfile.datafile.experimental.CountingBAInputStream;
import org.crococryptfile.datafile.experimental.DumpReaderStreamonly;
import org.crococryptfile.datafile.experimental.ItemZipper;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.suites.Suite;
import org.crococryptfile.suites.SuiteMODE;
import org.crococryptfile.suites.SuitePARAM;
import org.fhissen.utils.StreamCopy;
import org.fhissen.utils.StreamMachine;

public class CrocoWebDecryptor extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final String ENCODING = "UTF-8";
	
	private static boolean NOSSL_WARNING = true;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(false, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(true, req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(true, req, resp);
	}

	public void doit(boolean post, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(NOSSL_WARNING && !req.isSecure()){
			NOSSL_WARNING = false;
			System.err.println("A request is using an insecure (non-TLS/non-SSL) connection! " +
					"It is not recommended to transmit passwords in plaintext!");
		}
		
		req.setCharacterEncoding(ENCODING);
		resp.setCharacterEncoding(ENCODING);

		String path = req.getRequestURI();
		if(path == null) path = "";
		else path = path.trim();
		if(path.startsWith("/")) path = path.substring(1, path.length());
		if(path.endsWith("/")) path = path.substring(0, path.length() - 1);
		
		if (path.length() == 0){
			h_pageok(resp, PAGES.download);
			return;
		}
		
		PAGES page = null;
		try {
			page = PAGES.valueOf(path);
		} catch (Exception e) {}
		
		if(post){
			if(page == null){
				h_rootredirect(resp);
				return;
			}

			switch (page) {
			case download:
				actionUpload(req, resp);
				return;
				
			default:
				h_rootredirect(resp);
				return;
			}
		}
		else{
			if(page != null){
				switch (page) {
				case download:
					h_pageok(resp, PAGES.download);
					return;
					
				case error:
					h_pageok(resp, PAGES.error);
					return;
				}
			}
			
			if(path.startsWith(UI_RESPATH)){
				String file = path.substring(UI_RESPATH.length(), path.length());
				RES res  = null;
				try {
					res = resources.get(file);
				} catch (Exception e) {}

				if(res != null){
					h_res(resp, res);
				}
				else{
					h_rootredirect(resp);
				}
			}
			else{
				h_rootredirect(resp);
			}
		}
	}
	
	
	private static final void actionUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		h_nocache(resp);

		try {
			if(ServletFileUpload.isMultipartContent(req)){
			      ServletFileUpload upload = new ServletFileUpload();
			      upload.setHeaderEncoding(ENCODING);
			      
			      FileItemIterator iterator = upload.getItemIterator(req);
			      
			      String pass = null, name = null;
			      InputStream fileis = null;
			      
			      while (iterator.hasNext()) {
				        FileItemStream item = iterator.next();
				        
				        if (item.isFormField()) {
				        	if(item.getFieldName().equals(_p(PARAMS.password))){
				        		pass = StreamCopy.read(item.openStream()).trim();
				        	}
				        }
				        else {
				        	if(item.getFieldName().equals(_p(PARAMS.file))){
				        		if(pass == null || pass.length() < 8){
				        			h_errorredirect(resp);
				        			return;
				        		}

				        		fileis = item.openStream();
				        		name = item.getName();
				        		
						        if(name == null || name.length() == 0){
				        			h_errorredirect(resp);
								    return;
						        }

						        if(name.endsWith("\\")){
						        	name = "noname";
						        }
						        
						        int idx = name.lastIndexOf('\\') + 1;
						        if(idx > 0){
						        	name = name.substring(idx, name.length());
						        }

						        if(fileis == null){
				        			h_errorredirect(resp);
								    return;
						        }
						        
						        byte[] cache = null;
								byte[] buffer = new byte[1024 * 512];
								int readbytes = 0;
								int b = 0;
								ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
								while (b>=0 && readbytes <= CrocoWebDecryptorConfig.FILELENGTHLIMIT_BYTES){
									try {
										b = fileis.read(buffer);
										if (b>=0){
											readbytes += b;
											baos.write(buffer, 0, b);
										}
										else{
											fileis.close();
											break;
										}
									} catch (Exception e) {
					        			h_errorredirect(resp);
									    return;
									}			
								}

								if(readbytes > CrocoWebDecryptorConfig.FILELENGTHLIMIT_BYTES){
				        			h_errorredirect(resp);
								    return;
								}

								cache = baos.toByteArray();
								baos.reset();
								baos.close();
								h_nocache(resp);
								
								resp.setStatus(HttpServletResponse.SC_OK);
								resp.setContentType("application/octet-stream");
								resp.setHeader("Content-Disposition", "attachment; filename=\"DecryptArchive.zip\"");
								try {
									ItemZipper zip = new ItemZipper(resp.getOutputStream());
									Suite pbe = Suite.getInitializedInstance(SUITES.PBE1_AES, SuiteMODE.DECRYPT, SuitePARAM.password, pass.toCharArray());
									CountingBAInputStream thecis = new CountingBAInputStream(cache);
									InputStream theis = thecis.getIS();
									StreamMachine.read(theis, SUITES.MAGICNUMBER_LENGTH);
									pbe.readFrom(theis);
									DumpHeader dh = new DumpHeader(pbe);
									dh.readFrom(theis);
									theis.close();
									
									DumpReaderStreamonly rf = new DumpReaderStreamonly(pbe.headerLength() + dh.headerLength() + SUITES.MAGICNUMBER_LENGTH);
									rf.main(thecis, zip, pbe, dh);
									zip.close();
								} catch (Exception e) {
				        			h_errorredirect(resp);
								}
						        
							    return;
				        	}
				        }
			      }
			      
			      h_errorredirect(resp);
			}
			else{
    			h_errorredirect(resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			h_errorredirect(resp);
		}
	}

	
	private static final String UI_RESPATH = "res/";
	private static final String KEY_VAL = "###val###";

	enum PAGES{
		download,
		error,
		
		;
		
		private static HashMap<PAGES, String> cache = new HashMap<>();
		static{
			for(PAGES page: PAGES.values()){
				String tmp = StreamCopy.read(PAGES.class.getResourceAsStream(page.filename()));
				if(tmp == null) System.err.println("Page " + page.filename() + " does not exist");
				cache.put(page, tmp);
			}
		}
		
		@Override
		public String toString(){
			return cache.get(this);
		}
		
		public String msg(String val){
			return toString().replace(KEY_VAL, val);
		}
		
		public String filename(){
			return name() + ".html";
		}
	}
	
	enum PARAMS{
		id,
		password,
		file
		
		;
	}
	
	enum TYPE{
		css,
		png,
		gif,
		jpg,
		jpeg,
		ico,
		
		;
		
		public String getMimetype(){
			switch (this) {
			case css:
				return "text/css";
				
			case ico:
				return "image/x-icon";

			case jpg:
			case jpeg:
				return "image/jpeg";
				
			case png:
			case gif:
				return "image/" + name();

			default:
				return "application/octet-stream";
			}
		}
	}
	
	enum RES{
		all (TYPE.css),
		
		;
		
		private static HashMap<RES, byte[]> cache = new HashMap<>();
		static{
			for(RES res: RES.values()){
				byte[] tmp = StreamCopy.readBytes(RES.class.getResourceAsStream(res.filename()));
				if(tmp == null) System.err.println("Resource " + res.filename() + " does not exist");
				cache.put(res, tmp);
			}
		}

		private TYPE type;
		private RES(TYPE t){
			type = t;
		}
		
		public byte[] value(){
			return cache.get(this);
		}
		
		public void writeTo(OutputStream os){
			StreamCopy.writeBytes(os, value());
		}
		
		public String mimetype(){
			return type.getMimetype();
		}
		
		public String filename(){
			return name() + "." + type.name();
		}
	}
	
	private static HashMap<String, RES> resources = new HashMap<>();
	static{
		RES[] all = RES.values();
		for(RES res: all){
			resources.put(res.filename(), res);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private static final String _p(Enum p){
		return p.name();
	}

	private static final void h_nocache(HttpServletResponse resp){
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
		resp.setHeader("Pragma", "no-cache"); 
		resp.setDateHeader("Expires", 0); 
	}
	
	private static final void h_pageok(HttpServletResponse resp, PAGES page) throws IOException{
		h_pageok(resp, page.toString());
	}

	private static final void h_pageok(HttpServletResponse resp, String page) throws IOException{
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println(page);
	}

	private static final void h_res(HttpServletResponse resp, RES res) throws IOException{
		resp.setContentType(res.mimetype());
		res.writeTo(resp.getOutputStream());
	}

	private static final void h_rootredirect(HttpServletResponse resp) throws IOException{
		resp.sendRedirect("/");
	}

	private static final void h_errorredirect(HttpServletResponse resp) throws IOException{
		resp.sendRedirect("/" + PAGES.error.name());
	}
}
