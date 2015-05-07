package org.crocowebdec;


public class Launcher {
	public static void main(String[] args) throws Exception{
		int port = 0;
		if(args != null && args.length > 0){
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {}
		}
		if(port <= 0) port = 8888;
		
		boolean running = JettyStart.startup(port);
		
		if(!running){
			System.err.println("\n");
			System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println("Jetty could not be started. Is the port " + port + " already in use?");
			System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		else{
			System.out.println("\n");
			System.out.println("CrococryptFile WebDecryptor on Jetty is now up and running. You might open a browser now: http://localhost:" + port + "/");
		}
	}
}
