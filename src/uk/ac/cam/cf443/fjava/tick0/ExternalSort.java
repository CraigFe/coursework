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

	public static void sort(String filenameA, String filenameB) 
			throws FileNotFoundException, IOException {
		
		RandomAccessFile a1 = new RandomAccessFile(filenameA,"rw");
		RandomAccessFile b1 = new RandomAccessFile(filenameB,"rw");
		
		DataInputStream input = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(
								a1.getFD())));
	
		long mem = Runtime.getRuntime().freeMemory();
		long fileSize = a1.length(); //Size of the input file in bytes
		
		System.out.println("Available memory: " + mem);
		System.out.println("FileA: "+a1.length()+" bytes");
		System.out.println("FileB: "+b1.length()+" bytes");
			
		//Sort the file in memory
		if (mem > 2 * a1.length()) {
			System.out.println("File fits in memory");
			
			int intsToWrite = (int) (fileSize/4);
			
			ArrayList<Integer> buffer = new ArrayList<>();
			for (int i = 0; i < intsToWrite; i++) buffer.add(input.readInt());
	
			Collections.sort(buffer);
			
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(filenameA,false)));
			
			for (Integer i: buffer) output.writeInt(i);
			
			output.flush(); //Force buffered bytes to be written to the stream 
		
		//Cut the file into blocks, sort them in memory, then run mergesort on the blocks
		} else {
			System.out.println("File does not fit in memory");
	
			long blockSize  = (mem-10000)/4;                           //Number of integers per block
			int noBlocks    = (int) Math.ceil(fileSize/(4*blockSize)); //Number of blocks necessary
			
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(filenameB,false)));
			
			//Sort all blocks except the last
			for (int j = 1; j < noBlocks; j++) {
				
				ArrayList<Integer> buffer = new ArrayList<>();
				for (int i = 0; i < blockSize; i++) buffer.add(input.readInt());
				Collections.sort(buffer);
				for (Integer i: buffer) output.writeInt(i);
				
			}
			
			int intsToRead = (int) (fileSize/4 - (noBlocks-1)*blockSize); //Number of integers in the final block
			
			//Sort the last block
			ArrayList<Integer> buffer = new ArrayList<>();
			for (int j = 0; j < intsToRead; j++) {
				buffer.add(input.readInt());
				Collections.sort(buffer);
				for (Integer i: buffer) output.writeInt(i);
			}
			
			//Mergesort on blocks
			int mergeCount = 0;
			for (; blockSize < fileSize; blockSize*=2) {
				
				DataInputStream input1, input2;
				
				if (mergeCount % 2 == 0) { //Even number of merges => fileB contains data
					
					input1 = new DataInputStream(
							 	new BufferedInputStream(
							 		new FileInputStream(
										b1.getFD())));
					
					input2 = new DataInputStream(
							new BufferedInputStream(
									new FileInputStream(
											b1.getFD())));
					
					output = new DataOutputStream(
							new BufferedOutputStream(
									new FileOutputStream(filenameA,false)));
					
				} else { //Odd number of merges => fileA contains data

					input1 = new DataInputStream(
							new BufferedInputStream(
									new FileInputStream(
											a1.getFD())));
					
					input2 = new DataInputStream(
							new BufferedInputStream(
									new FileInputStream(
											a1.getFD())));
					
					output = new DataOutputStream(
							new BufferedOutputStream(
									new FileOutputStream(filenameB,false)));
					
				}
				
				//for (int offset = 0; offset < )
				
				//Seek second reader to correct position
				input2.skip(blockSize*4);
				
				//Mergesort using the three streams
				merge(input1,blockSize,input2,blockSize,output);
				mergeCount++;
			}
			
			if (mergeCount % 2 == 0) { //fileB contains data, copy to fileA for output
				
			}
			
		}
		return;
	}
	
	/**
	 * Takes two input streams and merges them together by writing them in
	 * non-decreasing order into an output stream
	 * 
	 * @param input1		The first input stream
	 * @param toWrite1		The number of integers to be taken from the first stream
	 * @param input2		The second input stream
	 * @param toWrite2		The number of integers to be taken from the second stream
	 * @param output		The output stream
	 * 
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void merge(DataInputStream input1, long toWrite1, DataInputStream input2, long toWrite2, DataOutputStream output) 
			throws FileNotFoundException, IOException {
		

		int l = input1.readInt();
		int r = input2.readInt();
		toWrite1--; toWrite2--;
		
		
		
		while (toWrite1 != 0 && toWrite2 != 0) {
			
			if (l <= r) {
				
				output.writeInt(l);
				l = input1.readInt();
				toWrite1--;
				
			} else { // r < l
				
				output.writeInt(r);
				r = input2.readInt();
				toWrite2--;
				
			}

		}

		//Write remaining integers from left and right partitions
		for (; toWrite1 > 0; toWrite1--) {
			
			output.writeInt();
		}
		for (; toWrite2 > 0; toWrite2--) output.writeInt(l)
		

		
		output.flush();
	}
	
	
}