package uk.ac.cam.cf443.fjava.tick0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class ExternalSort {

	public static void sort(String filenameA, String filenameB) 
			throws FileNotFoundException, IOException {
		
		RandomAccessFile fileA = new RandomAccessFile(filenameA,"rw");
		RandomAccessFile fileB = new RandomAccessFile(filenameB,"rw");
		
		DataInputStream input = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(
								fileA.getFD())));
	
		long availableMemory = Runtime.getRuntime().freeMemory();
		long fileSize = fileA.length(); //Size of the input file in bytes
		
		System.out.println("Available memory: " + availableMemory);
		System.out.println("FileA: "+fileA.length()+" bytes");
		System.out.println("FileB: "+fileB.length()+" bytes");
			
		//Sort the file in memory
		if (availableMemory > 2 * fileA.length()) {
			System.out.println("File fits in memory");
			
			
			int intsToSort = (int) (fileSize / 4);
			ArrayList<Integer> buffer = new ArrayList<>(intsToSort);
			
			for (int i = 0; i < intsToSort; i++) buffer.add(input.readInt());
			Collections.sort(buffer);
			
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filenameA,false)));
			for (Integer i: buffer) output.writeInt(i);
			output.flush();
			output.close();
			
		
		//Cut the file into blocks, sort them in memory, then run mergesort on the blocks
		} else {
			
			long blockSize      = (long) (availableMemory*0.1/4);           //Number of integers per block
			int noBlocks        = (int) Math.ceil(fileSize/(4f*blockSize)); //Number of blocks necessary
			long finalBlockSize = fileSize/4 - (noBlocks-1)*blockSize;      //Number of integers in final block

			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filenameB,false)));
			PriorityQueue<FiniteFileStream> minHeap = new PriorityQueue<FiniteFileStream>();
	
			//Loop through all blocks except the last
			for (int j = 0; j < noBlocks-1; j++) {
				
				//Sort block in memory
				sortInMemory(input,output,blockSize);
				
				//Add block stream to minheap
				DataInputStream blockStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filenameB)));
				blockStream.skip(j*blockSize*4); //Skip to correct position
				minHeap.add(new FiniteFileStream(blockStream,blockSize));
				
			}
			
			
			//Sort final block
			sortInMemory(input,output,finalBlockSize);
			
			
			//Add final block stream to minheap
			DataInputStream finalBlockStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filenameB)));
			finalBlockStream.skip((noBlocks-1)*blockSize*4); //Skip to start of final block
			minHeap.add(new FiniteFileStream(finalBlockStream,finalBlockSize));
			
			
			//Now write to fileA
			output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filenameA,false)));
			

			//Keep writing elements until all elements have been written
			while (!minHeap.isEmpty()) {
				
				FiniteFileStream minStream = minHeap.poll();	//Get stream with smallest head element
				output.writeInt(minStream.poll());				//Write head element to output
				if (minStream.hasNext) minHeap.add(minStream);	//If stream is not empty, add to min heap again
				
				
			}
			output.flush();
			output.close();
		}
		
		fileA.close();
		fileB.close();
	}
	
	//Loads intsToSort many integers into memory, sorts them, then writes them to the output stream
	private static void sortInMemory(DataInputStream input, DataOutputStream output, long intsToSort) throws IOException {
		ArrayList<Integer> buffer = new ArrayList<>((int) intsToSort);
		
		for (int i = 0; i < intsToSort; i++) buffer.add(input.readInt());
		Collections.sort(buffer);
		for (Integer i: buffer) output.writeInt(i);
		output.flush();
	
	}
}