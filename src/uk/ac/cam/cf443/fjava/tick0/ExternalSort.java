package uk.ac.cam.cf443.fjava.tick0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;



public class ExternalSort {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		for(int i = 1; i <= 10; i++) test(i);
	}
	
	private static void test(int n) throws FileNotFoundException, IOException {
		System.out.println("--------------------------------------------");
		System.out.println("Test "+n);
		System.out.println();
		
		String filepath = "src/uk/ac/cam/cf443/fjava/tick0/test-suite/";
		
		//Create a tmp fileA to work on
		File src = new File(filepath+"test"+n+"a.dat");
		File dst = new File(filepath+"test"+n+"a-copy.dat");
		Files.copy(src.toPath(), dst.toPath());
		    
		long startTime = System.nanoTime();
		
		
		sort(filepath+"test"+n+"a-copy.dat",filepath+"test"+n+"b.dat");
		
		System.out.println("The checksum is "+checkSum(filepath+"test"+n+"a-copy.dat"));
		System.out.println("Process completed in "+(System.nanoTime()-startTime)/1000/1000.00+"ms.");
	}

	public static void sort(String filenameA, String filenameB) 
			throws FileNotFoundException, IOException {
		
		RandomAccessFile a1 = new RandomAccessFile(filenameA,"rw");
		RandomAccessFile a2 = new RandomAccessFile(filenameA,"rw");
		RandomAccessFile b1 = new RandomAccessFile(filenameB,"rw");
		RandomAccessFile b2 = new RandomAccessFile(filenameB,"rw");
		
		DataInputStream input = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(
								a1.getFD())));

		long mem = Runtime.getRuntime().freeMemory();
		long req = a1.length();
		
		System.out.println("Available memory:" + mem);
		System.out.println("FileA: "+a1.length());
		System.out.println("FileB: "+b1.length());
		
		
		if (req == 0)  {
			System.out.println("File is empty");
			
			
		//Sort the file in memory
		} else if (mem > 1.1 * a1.length()) {
			System.out.println("File fits in memory");
			
			int num = (int) (req/4);
			
			ArrayList<Integer> buffer = new ArrayList<>();
			for (int i = 0; i < num; i++) buffer.add(input.readInt());
			
			Collections.sort(buffer);
			
			for (Integer i: buffer) a1.writeInt(i);
			
			
	
		} else {
			System.out.println("File does not fit in memory");
			
			long blockSize = req;
			if (mem < req) blockSize = (long) (mem-1000)/2;
			
			int[] buffer = new int[(int) ((blockSize-1000)/4)];

			
			
		}
		
		return;
		
		
	}
	
	
	private static String checkSum(String filename) {

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			DigestInputStream ds = new DigestInputStream(
					new FileInputStream(filename), md);
			byte[] b = new byte[512];
			while (ds.read(b) != -1)
				;

			String computed = "";
			for(byte v : md.digest()) 
				computed += byteToHex(v);

			return computed;
			
		} 
		catch (NoSuchAlgorithmException e) {e.printStackTrace();} 
		catch (FileNotFoundException e)    {e.printStackTrace();} 
		catch (IOException e) 			   {e.printStackTrace();}
		
		return "<error computing checksum>";
	}
	
	private static String byteToHex(byte b) {
		String r = Integer.toHexString(b);
		if (r.length() == 8) return r.substring(6);
		return r;
	}

}
