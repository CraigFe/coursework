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
import static java.nio.file.StandardCopyOption.*;



public class ExternalSort {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		for(int i = 1; i <= 6; i++) test(i);
	}
	
	private static void test(int n) throws FileNotFoundException, IOException {
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Test "+n);
		System.out.println();
		
		String filepath = "src/uk/ac/cam/cf443/fjava/tick0/test-suite/";
		
		//Create a tmp fileA to work on
		File src = new File(filepath+"test"+n+"a.dat");
		File dst = new File(filepath+"test"+n+"a-copy.dat");
		Files.copy(src.toPath(), dst.toPath(), REPLACE_EXISTING);
		
		//Likewise for fileB
	    src = new File(filepath+"test"+n+"b.dat");
		dst = new File(filepath+"test"+n+"b-copy.dat");
		Files.copy(src.toPath(), dst.toPath(), REPLACE_EXISTING);
		
		
		long startTime = System.nanoTime();
		
		sort(filepath+"test"+n+"a-copy.dat",filepath+"test"+n+"b-copy.dat");
		
		System.out.println();
		System.out.println("Process completed in "+(System.nanoTime()-startTime)/1000/1000.00+"ms.");
		System.out.println();
		System.out.println("Checksums: ");
		System.out.println("   init "+checkSum(filepath+"test"+n+"a.dat"));
		System.out.println("  final "+checkSum(filepath+"test"+n+"a-copy.dat"));
		
		if (checkSum(filepath+"test"+n+"a-copy.dat").equals(checksums[n-1])) {
			System.out.println("VALID");
		} else {
			System.out.println("INVALID");
			Runtime.getRuntime().halt(0);
		}
		
		
	}

	public static void sort(String filenameA, String filenameB) 
			throws FileNotFoundException, IOException {
		
		RandomAccessFile a1 = new RandomAccessFile(filenameA,"rw");
		RandomAccessFile b1 = new RandomAccessFile(filenameB,"rw");
		
		DataInputStream input = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(
								a1.getFD())));
		
		DataOutputStream output = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(
								a1.getFD())));

		a1.seek(0);
		long mem = Runtime.getRuntime().freeMemory();
		long req = a1.length();
		
		System.out.println("Available memory:" + mem);
		System.out.println("FileA: "+a1.length()+" bytes");
		System.out.println("FileB: "+b1.length()+" bytes");
			
		//Sort the file in memory
		if (mem > 1.2 * a1.length()) {
			System.out.println("File fits in memory");
			
			int num = (int) (req/4);
			
			ArrayList<Integer> buffer = new ArrayList<>();
			for (int i = 0; i < num; i++) buffer.add(input.readInt());
	
			Collections.sort(buffer);
			System.out.println(buffer.toString());
			for (Integer i: buffer) output.writeInt(-1);
			
			output.flush(); //Force buffered bytes to be written to the stream 
			
		} else {
			System.out.println("File does not fit in memory");
			
			/*long blockSize = req;
			if (mem < req) blockSize = (long) (mem-1000)/2;
			
			int[] buffer = new int[(int) ((blockSize-1000)/4)];*/
			
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
	
	//md5 checksums of the sorted files
	private static String[] checksums = {
		"d41d8cd98f0b24e980998ecf8427e",
		"a54f041a9e15b5f25c463f1db7449",
		"c2cb56f4c5bf656faca0986e7eba38",
		"c1fa1f22fa36d331be4027e683baad6",
		"8d79cbc9a4ecdde112fc91ba625b13c2",
		"1e52ef3b2acef1f831f728dc2d16174d",
		"6b15b255d36ae9c85ccd3475ec11c3",
		"1484c15a27e48931297fb6682ff625",
		"ad4f60f065174cf4f8b15cbb1b17a1bd",
		"32446e5dd58ed5a5d7df2522f0240",
		"435fe88036417d686ad8772c86622ab",
		"c4dacdbc3c2e8ddbb94aac3115e25aa2",
		"3d5293e89244d513abdf94be643c630",
		"468c1c2b4c1b74ddd44ce2ce775fb35c",
		"79d830e4c0efa93801b5d89437f9f3e",
		"c7477d400c36fca5414e0674863ba91",
		"cc80f01b7d2d26042f3286bdeff0d9"
		
	};
	
}
