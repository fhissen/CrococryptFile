package org.fhissen.utils.ui;


public class StatusDebug implements StatusUpdate{
	@Override
	public void receiveProgress(int perc) {
		System.out.println("% " + perc + " (main)");
	}
	
	@Override
	public void receiveDetailsProgress(int perc) {
		System.out.println("% " + perc + " (sub)");
	}

	@Override
	public void receiveMessageSummary(String msg) {
		System.out.println("S " + msg);
	}
	
	@Override
	public void receiveMessageDetails(String msg) {
		System.out.println("D " + msg);
	}

	@Override
	public void finished() {
		System.out.println("Status invalidated");
	}
	
	@Override
	public void start() {
		System.out.println("Status starts");
	}
	
	@Override
	public boolean isActive() {
		return true;
	}
}
