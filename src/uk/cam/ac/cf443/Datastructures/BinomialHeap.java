package uk.cam.ac.cf443.Datastructures;

import java.util.AbstractQueue;
import java.util.Iterator;

public final class BinomialHeap<E extends Comparable<? super E>> extends AbstractQueue<E> {
	
	private Node<E> head;
	public BinomialHeap() {head = new Node<E>();}
	
	@Override
	public int size() {
		int result = 0;
		for (Node<?> node = head.next; node != null; node = node.next) {
			if (node.rank >= 31) throw new ArithmeticException("Size overflow");
			result |= 1 << node.rank;
		}
		
		return result;
	}

	@Override
	public E peek() {
		E result;
		
		for (Node<E> node = head.next; node != null; node = node.next) {
			if (result == null || node.value.compareTo(result) < 0)
				result = node.value;
		}
		
		return result;
	}
	
	@Override
	public E poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean offer(E arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	





	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}



}

//A binomial heap node, containing a value of Generic type E
private static final class Node<E> {
	public E value;
	public int rank;
	public Node<E> child;
	public Node<E> next;
	
	//Sentinel node
	public Node() {
		this(null); 
		rank = -1;
	}
	
	public Node(E val) {value = val;}
	
	public Node<E> removeRoot() {
		Node<E> result;
		Node<E> node = child;
		
		while (node != null) { 
			Node<E> next = node
		}
		
	}
}