package uk.ac.cam.cf443.fjava.tick0;

import java.io.DataInputStream;
import java.io.IOException;

public class FiniteFileStream implements Comparable<FiniteFileStream> {
	private DataInputStream input;
	private int head;
	private long toRead;
	
	public boolean hasNext;
	
	public FiniteFileStream(DataInputStream input, long toRead) throws IOException {
		this.input = input;
		this.head = input.readInt();
		this.toRead = --toRead;
		this.hasNext = true;
	}
	
	public int peek() {return head;}
	
	public int poll() throws IOException {
		if (!hasNext) throw new IOException("Attempted to poll empty stream");
		
		int tmp = head;
		
		if (toRead > 0) {
			head = input.readInt();
			toRead--;
		} else {
			hasNext = false;
		}
		
		return tmp;
		
	}
	
	@Override
	public int compareTo(FiniteFileStream arg0) {
		return Integer.compare(this.head, arg0.peek());
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.head);
	}
	
	public int getSize() {
		return (int) (toRead+1);
	}
}
