package org.crococryptfile.ui.resources;

import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.fhissen.settings.KeyvalueFile;
import org.fhissen.utils.Codes;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.os.OSFolders;


public class ResourceCenter {
	@SuppressWarnings("rawtypes")
	private static final Class THECLASS = ResourceCenter.class;

	private static final String PATH_BASE = "org/crococryptfile/ui/resources/";
	
	private static final String PATH_TEXTS = PATH_BASE + "texts/";
	private static final String PATH_HTML = PATH_BASE + "html/";
	private static final String PATH_IMAGES = PATH_BASE + "images/";

	private static final String BASE, ABSOLUTE_HTML, ABSOLUTE_IMAGES;

	public static final String TITLE = "CrococryptFile";
	public static final String VERSION = "1.6";
	public static final String VERSIONCODE = "6";
	
	public static final String TITLE_VERSION = TITLE + " " + VERSION;
	
	public static final ArrayList<Image> icons = new ArrayList<>();
	public static Image logo16, logo32, logo48, logo64;

	public static Image optsup, optsdown;

	static{
		String tmp = ResourceCenter.getAbsoluteRes("x").toString();
		BASE = tmp.substring(0, tmp.length()-1);
		ABSOLUTE_HTML = BASE + PATH_HTML;
		ABSOLUTE_IMAGES = BASE + PATH_IMAGES;
		
		try {
			logo16 = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "logo16.png")).getImage();
			logo32 = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "logo32.png")).getImage();
			logo48 = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "logo48.png")).getImage();
			logo64 = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "logo64.png")).getImage();

			icons.add(ResourceCenter.logo64);
			icons.add(ResourceCenter.logo32);
			icons.add(ResourceCenter.logo48);
			icons.add(ResourceCenter.logo16);

			optsup = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "optsup.png")).getImage();
			optsdown = new ImageIcon(getAbsoluteRes(PATH_IMAGES + "optsdown.png")).getImage();
		} catch (Throwable t) {
			System.err.println(t.getLocalizedMessage());
		}
	}
	
	public static final String getAbsoluteBase(){
		return BASE;
	}
	
	public static final String getHTMLAbsolute(){
		return ABSOLUTE_HTML;
	}
	
	public static final String getHTMLRelative(){
		return PATH_HTML;
	}
	
	public static final String getImagesAbsolute(){
		return ABSOLUTE_IMAGES;
	}
	
	public static final String getImagesRelative(){
		return PATH_IMAGES;
	}
	
	public static final String getTexts(){
		return PATH_TEXTS;
	}
	
	
	public static final URL getAbsoluteRes(String path){
		return THECLASS.getResource("/" + path);
	}
	
	public static final InputStream getAbsoluteStreamRes(String path){
		return THECLASS.getResourceAsStream("/" + path);
	}

	
	private static File datafolder, whereami, portable_container;
	private static String s_portable_container;
	private static KeyvalueFile settings;
	
	private static Locale locale;
	private static String locale_code;
	
	static{
		try {
			try {
				String decodedPath = URLDecoder.decode(THECLASS.getProtectionDomain().getCodeSource().getLocation().getPath(), Codes.UTF8);
				whereami = new File(decodedPath).getParentFile();

				File lang = new File(whereami, "lang");
				if(lang.exists() && lang.isFile()){
					String tmp = FileUtils.readFile(lang);
					if(tmp != null && tmp.length() > 0){
						tmp = tmp.trim();
						locale = new Locale(tmp);
					}
				}
				if(locale == null) locale = Locale.ENGLISH;
				locale_code = locale.getLanguage();
				
				try {
					Locale.setDefault(locale);
				} catch (Exception e) {
					Locale.setDefault(Locale.ENGLISH);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			datafolder = OSFolders.makeUserAppfolder(TITLE);
			
			settings = new KeyvalueFile(new File(datafolder, "settings"));
			settings.load();
		} catch (Throwable t) {
			System.err.println(t.getLocalizedMessage());
		}
	}
	
	public static final File getDatafolder(){
		return datafolder;
	}
	
	public static final File getWhereiam(){
		return whereami;
	}
	
	public static final File getPortableContainer(){
		return portable_container;
	}
	
	public static final String getPortableContainerString(){
		return s_portable_container;
	}
	
	public static final KeyvalueFile getSettings(){
		return settings;
	}

	public static final Locale getLocale(){
		return locale;
	}
	
	public static final String getLocaleCode(){
		return locale_code;
	}
	
	public static final File make(String file_path){
		return new File(datafolder, file_path);
	}
}
