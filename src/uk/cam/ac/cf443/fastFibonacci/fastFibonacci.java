package uk.cam.ac.cf443.fastFibonacci;

import java.math.BigInteger;

public final class fastFibonacci {
	
	//Speed test of the 3 Fibonacci algorithms
	public static void main(String... ignored) {
		
	}
	
	//Standard recursive method
	private static BigInteger doubling(int n) {
		
	}
	
	//Matrix multiplication method
	private static BigInteger matrix(int n) {
		
	}
	
	//Dynamic programming method
	private static BigInteger dynamic(int n) {
		BigInteger[] fibs = new BigInteger[3];
		fibs[0] = BigInteger.ZERO;
		fibs[1] = BigInteger.ONE;
		
		for (int i = 0; i < n; i++) {
			fibs[2] = fibs[0].add(fibs[1]);
			fibs[0] = fibs[1];
			fibs[1] = fibs[2];
		}
		
		return fibs[0];
	}
	
	//Recursive method
	private static BigInteger recursive(int n) {
		if (n==0 || n==1) return BigInteger.valueOf(n);
		return recursive(n-1).add(recursive(n-2));	
	}
}