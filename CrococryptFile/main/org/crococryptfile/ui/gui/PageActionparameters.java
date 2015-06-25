package org.crococryptfile.ui.gui;

import java.util.ArrayList;
import java.util.HashMap;

public class PageActionparameters {
	private HashMap<Object, Object> params = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public void add(Object key, Object val){
		Object orgval = params.get(key);
		
		if(orgval != null){
			if(orgval instanceof ArrayList<?>){
				((ArrayList<Object>)orgval).add(val);
			}
			else{
				ArrayList<Object> arr = new ArrayList<>();
				arr.add(params.remove(key));
				arr.add(val);
				params.put(key, arr);
			}
		}
		else{
			params.put(key, val);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getList(Object key){
		return (ArrayList<Object>) params.get(key);
	}
	
	public String getString(Object key){
		return (String) params.get(key);
	}
	
	public Object getRaw(Object key){
		return params.get(key);
	}
	
	public boolean isList(Object key){
		Object orgval = params.get(key);
		return orgval != null && orgval instanceof ArrayList<?>;
	}
	
	public boolean isString(Object key){
		Object orgval = params.get(key);
		return orgval != null && orgval instanceof String;
	}
	
	public boolean exists(Object key){
		return params.get(key) != null;
	}
}
