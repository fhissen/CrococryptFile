package org.fhissen.crypto;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.KeyGenerator;

import org.fhissen.utils.ByteUtils;


public class KeygenUtils {
	private MessageDigest md;
	private boolean hasgui = false;

	{
		try {
			md = MessageDigest.getInstance(CryptoCodes.HASH_SHA512);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		hasgui = !GraphicsEnvironment.isHeadless();
	}
	
	public SecureRandom makeSR(){
		SecureRandom sr = new SecureRandom();
		
		sr.nextBoolean();
		byte[] b = additionalSeed();
		
		sr.setSeed(b);
		
		CryptoUtils.kill(b);
		
		return sr;
	}
	
	public KeyGenerator make(int sizeinbits) {
		try {
			KeyGenerator key_gen = KeyGenerator.getInstance(CryptoCodes.KEY_AES);
			key_gen.init(sizeinbits, makeSR());
			long max = (System.currentTimeMillis() % 11) + 1;
			
			
			for(int i=0; i < max; i++){
				key_gen.generateKey();
			}

			return key_gen;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] raw(int sizeinbits) {
		return make(sizeinbits).generateKey().getEncoded();
	}
	
	private final byte[] additionalSeed(){
		byte[] addpseudorandomness;
		addpseudorandomness = newdigestNwipe(Calendar.getInstance().toString().getBytes());
		
		ArrayList<Object> collector = new ArrayList<>();
		try {
			collector.add(System.nanoTime());
			collector.add((long)new Object().hashCode() * (long)new Object().hashCode());
			try {collector.add(new File(".").getFreeSpace());} catch (Exception e) {}
			if(hasgui) collector.add(MouseInfo.getPointerInfo().getLocation().x);
			if(hasgui) collector.add(MouseInfo.getPointerInfo().getLocation().y);
			try {collector.add(new File(".").getUsableSpace());} catch (Exception e) {}
			if(hasgui) {
				Color tmp = new Robot().getPixelColor(10 + ((int)System.nanoTime() % 2000), 10 + ((int)System.nanoTime() % 1000));
				collector.add(tmp.getRGB());
				collector.add(tmp.getTransparency());
				collector.add(tmp.hashCode());
				tmp = null;
			}
			collector.add((long)new Object().hashCode() * (long)new Object().hashCode());
			if(hasgui){
				Color tmp = new Robot().getPixelColor(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
				collector.add(tmp.getRGB());
				collector.add(tmp.getTransparency());
				collector.add(tmp.hashCode());
				tmp = null;
			}
			collector.add(System.currentTimeMillis());
			try {collector.add(Runtime.getRuntime().availableProcessors());} catch (Exception e) {}
			collector.add((long)new Object().hashCode() * (long)new Object().hashCode());
			collector.add(Runtime.getRuntime().hashCode());
			collector.add((long)new Object().hashCode() * (long)new Object().hashCode());
		} catch (Exception e) {}
		addpseudorandomness = newdigestNwipe(Xor.xorWipe(addpseudorandomness, ByteUtils.copyTogetherNwipeO(collector.toArray(new Object[collector.size()]))));
		collector.clear();
		System.gc();
		
		addpseudorandomness = newdigestNwipe(Xor.xorWipe(addpseudorandomness, ByteUtils.copyTogetherNwipeB(pseudoheap(),
				pseudorun(101), pseudorun(102), pseudorun(103), pseudorun((int)(System.nanoTime() % 111)))));
		System.gc();
		
		return addpseudorandomness;
	}
	
	private final byte[] newdigestNwipe(byte[] inarr){
		byte[] arr = md.digest(inarr);
		
		CryptoUtils.kill(inarr);
		
		return arr;
	}
	
	private static final byte[] pseudoheap(){
		long curheap = Runtime.getRuntime().totalMemory(); 
		
		long maxheap = Runtime.getRuntime().maxMemory();
		
		long freeheap = Runtime.getRuntime().freeMemory(); 
		
		return ByteUtils.copyTogetherNwipeO(curheap, maxheap, freeheap);
	}

	private static int winner;
	private static final class MyRun implements Runnable{
		private int no;
		
		public MyRun(int no){
			this.no = no;
		}
		
		@Override
		public void run() {
			no += (System.currentTimeMillis() % Integer.MAX_VALUE);
			winner = no + hashCode();
		}
	}
	
	private static final byte[] pseudorun(int len){
		try {
			Thread[] ts = new Thread[len];
			for(int i=0; i<len; i++){
				ts[i] = new Thread(new MyRun(i));
			}

			for(int i=0; i<len; i++){
				ts[i].start();
			}

			for(int i=0; i<len; i++){
				ts[i].join();
				ts[i] = null;
			}
			
			byte[] ret = ByteUtils.intToBytes(winner);
			winner = Integer.MIN_VALUE;
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[]{-1};
		}
	}
}
