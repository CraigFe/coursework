package uk.cam.ac.cf443.Datastructures;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.sun.istack.internal.NotNull;

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
		hds = new LinkedList<E>(); //Must have O(1) removal at head
		tls = new LinkedList<E>(); //Must have O(1) insertion at head
	}

	@Override
	public boolean offer(@NotNull E e) {
		return hds.add(Objects.requireNonNull(e));
	}

	@Override
	public E peek() {
		if (size == 0) return null;
		normalise();
		return hds.get(0);
	}

	@Override
	public E poll() {
		if (size == 0) return null;
		normalise(); //Ensure that the hds list is not empty
		return hds.remove(0);
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> it = new Iterator<E>() {

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public E next() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}

	@Override
	public int size() {return size;}
	
	//Normalises the queue by flipping the tls list and appending it to hds
	private boolean normalise() {
		if (hds.isEmpty()) {
			Collections.reverse(tls);
			hds.addAll(tls);
			tls.clear();
			return true;
		}
		return false;
	}
	
	
}