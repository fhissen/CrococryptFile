package org.crococryptfile.ui.gui;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.crococryptfile.ui.resources.ResourceCenter;
import org.fhissen.utils.StreamCopy;

public abstract class Page {
	public static final String HTML_PARAM_SEP = "###";
	public static final String HTML_FORM_SEP = "\\Q?|?\\E";
	private static final String KEYWORD_ACTION = "action_";
	private static final String KEYWORD_PARAM = "param_";

	protected abstract void action(PageLauncher pl, Object action, HashMap<Object, String> params);
	protected abstract void generate(HashMap<String, String> uiparams);
	
	protected Object extractAction(String tmp){
		if(isAction(tmp)) return tmp.substring(KEYWORD_ACTION.length(), tmp.length());
		return null;
	}

	protected Object extractParam(String tmp){
		if(isParam(tmp)) return tmp.substring(KEYWORD_PARAM.length(), tmp.length());
		return null;
	}
	
	public Dimension getSize(){
		return new Dimension(425, 300);
	}
	
	public final String HTML(PageLauncher pl){
		this.pl = pl;

		HashMap<String, String> params = new HashMap<>();
		generate(params);
		return setParameters(loadPage(), params);
	}
	
	private PageLauncher pl;
	public final PageLauncher getPageLauncher(){
		return pl;
	}
	
	public final void exit(){
		if(pl != null) pl.exit();
	}

	public static final void exitPage(Object source){
		if(source == null || !(source instanceof Page)) return;
		
		((Page)source).exit();
	}
	
	
	protected final boolean isAction(String tmp){
		if(tmp == null || tmp.length() == 0) return false;
		return tmp.startsWith(KEYWORD_ACTION);
	}
	
	protected final boolean isParam(String tmp){
		if(tmp == null || tmp.length() == 0) return false;
		return tmp.startsWith(KEYWORD_PARAM);
	}
	
	private String cachebase;
	private final String loadPage(){
		try {
			String tmp = cachebase; 
			if(cachebase == null){
				tmp = StreamCopy.read(ResourceCenter.getAbsoluteStreamRes(file()));
				if(tmp == null) return "Page resource for " + name() + " not found!";
				cachebase = tmp;
			}
			
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return name();
		}
	}
	
	private final String name(){
		return this.getClass().getSimpleName().toLowerCase();
	}

	private final String file(){
		String name = name();
		String locale_file = ResourceCenter.getHTMLRelative() + name + "_" + ResourceCenter.getLocaleCode() + ".html";
		InputStream tmp = ResourceCenter.getAbsoluteStreamRes(locale_file);
		if(tmp == null)
			locale_file = ResourceCenter.getHTMLRelative() + name + ".html";
		
		return locale_file;
	}
	
	
	protected static final String _quoteString(String tmp){
		return tmp.replace("\"", "&quot;");
	}
	
	protected static final int _s2i(String tmp){
		int ret = -1;
		try {
			ret = Integer.parseInt(tmp);
		} catch (Exception e) {
			return -1;
		}
		return ret;
	}

	private static final String setParameters(String raw, HashMap<String, String> params){
		if(params != null && params.size() > 0){
			Iterator<String> it = params.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				raw = raw.replace(HTML_PARAM_SEP+key+HTML_PARAM_SEP, params.get(key));
			}
		}
		
		raw = raw
				.replace(HTML_PARAM_SEP + "base" + HTML_PARAM_SEP, ResourceCenter.getAbsoluteBase())
				.replace(HTML_PARAM_SEP + "pathhtml" + HTML_PARAM_SEP, ResourceCenter.getHTMLAbsolute())
				.replace(HTML_PARAM_SEP + "pathimages" + HTML_PARAM_SEP, ResourceCenter.getImagesAbsolute())
				;

		return raw;
	}
}
