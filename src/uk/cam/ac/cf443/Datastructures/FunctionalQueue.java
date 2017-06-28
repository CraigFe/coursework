package uk.cam.ac.cf443.Datastructures;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The FunctionalQueue structure is an implementation of
 * 
 * @author cf443
 *
 * @param <E>
 */
public final class FunctionalQueue<E> extends AbstractQueue<E> {
	
	private List<E> hds;
	private List<E> tls;
	private int size;
	
	//Constructor
	public FunctionalQueue() {
		hds = new ArrayList<E>();
		tls = new ArrayList<E>();
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {return size;}
	
	
}