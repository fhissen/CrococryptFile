package org.crococryptfile.ui.gui.pages;

import java.util.HashMap;

import org.crococryptfile.Settings;
import org.crococryptfile.suites.capirsaaes.CAPIRSAUtils;
import org.crococryptfile.suites.capirsaaes.CAPIRSAUtils.CAPIRSAAliases;
import org.crococryptfile.ui.gui.Page;
import org.crococryptfile.ui.gui.PageActionparameters;
import org.crococryptfile.ui.gui.PageLauncher;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;

public class CAPI_DNList extends Page{
	private SimpleCallback<String> cb;
	private CAPIRSAAliases dnlist = CAPIRSAUtils.getAll();

	public CAPI_DNList(SimpleCallback<String> cb){
		this.cb = cb;
	}
	
	@Override
	protected void generate(HashMap<String, String> params){
		String listui = null;
		String lastdnalias = ResourceCenter.getSettings().get(Settings.lastcapidn);
		
		if(dnlist == null || dnlist.aliases.size() == 0){
			listui = _T.CAPIDNListWindow_nokeys.val();
		}
		else{
			final String listbase = "<option SELECTED value=\"CODE\">DESCR</option>";
			StringBuilder sbmenu = new StringBuilder();
			for(int i=0; i<dnlist.displaytexts.size(); i++){
				String tmp = listbase;
				if(!dnlist.aliases.get(i).equals(lastdnalias)) tmp = tmp.replace("SELECTED", "");
				tmp = tmp.replace("CODE", i+"");
				tmp = tmp.replace("DESCR", dnlist.displaytexts.get(i));
				sbmenu.append(_quoteString(tmp));
			}
			listui = sbmenu.toString();
		}
		
		params.put("dnlist", listui);
		params.put("title", _T.CAPIDNListWindow_title.val());
		params.put("text", _T.DecryptWindow_text.val());
	}
	
	@Override
	protected void action(PageLauncher pl, Object action, PageActionparameters params) {
		if(params == null || action == null || !action.equals("ok")) return;
		
		if(dnlist == null || dnlist.aliases.size() == 0){
			pl.exit();
			return;
		}
		
		int i = _s2i(params.getString("alias"));
		if(i >= 0){
			String lastdnalias = ResourceCenter.getSettings().get(Settings.lastcapidn);
			if(!dnlist.aliases.get(i).equals(lastdnalias)){
				ResourceCenter.getSettings().set(Settings.lastcapidn, dnlist.aliases.get(i));
				ResourceCenter.getSettings().save();
			}
			cb.callbackValue(this, dnlist.aliases.get(i));
		}
		else{
			cb.callbackValue(this, null);
		}
		
		pl.exit();
	}
}
