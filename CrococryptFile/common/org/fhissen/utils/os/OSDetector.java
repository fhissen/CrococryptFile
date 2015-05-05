package org.fhissen.utils.os;

public class OSDetector {
	public enum OS{
		WIN,
		LIN,
		MAC,
		
		;
	}
	
	private static final OS current;
	static{
		String tmp = System.getProperty("os.name");
		
		if(tmp == null){
			current = OS.LIN;
		}
		else{
			tmp = tmp.toLowerCase();
			
			if(tmp.contains("windows")){
				current = OS.WIN;
			}
			else if(tmp.contains("linux")){
				current = OS.LIN;
			}
			else{
				if(tmp.contains("mac os") || tmp.contains("macos") ){
					current = OS.MAC;
				}
				else{
					String vendor = System.getProperty("java.vendor");
					if(vendor != null && vendor.toLowerCase().contains("apple")){
						current = OS.MAC;
					}
					else{
						current = OS.LIN;
					}
				}
			}
		}
	}

	public static final OS which() {
		return current;
	}
	
	public static final boolean isWin(){
		return current == OS.WIN;
	}

	public static final boolean isMac(){
		return current == OS.MAC;
	}

	public static final boolean isLin(){
		return current == OS.LIN;
	}
}
