package uk.cam.ac.cf443.FastFibonacci;

import java.math.BigInteger;

/**
 * A stateless function object. Containing 4 implementations of the Fibonacci function, which
 * takes an integer n and returns the nth element of the Fibonacci sequence:
 * 	0, 1, 1, 2, 3, 5, 8, ...
 * 
 * @author cf443
 */
public final class FastFibonacci {
	
	//Speed test of the 3 Fibonacci algorithms
	public static void main(String... ignored) {
		
	}
	
	/*
	 * Fast doubling method. Logically equivalent to the matrix method, with the redundant
	 * calculations eliminated. Implemented non-recursively to prevent stack overflow on
	 * exponentially large inputs.
	 * 
	 * F(2n)   = F(n) * (2*F(n+1) - F(n))
	 * F(2n_1) = F(n+1)^2 + F(n)^2
	 */
	private static BigInteger doubling(int n) {
		int m = 0;
		return null;
		
	}
	
	/*
	 * Matrix multiplication method. Encodes the definition of the Fibonacci numbers using 
	 * matrices. Much faster than the dynamic programming method, but is slower than the doubling
	 * method by a constant factor due to redundant calculations in the matrix multiplications.
	 * 
	 * |1 1|^n   |F(n+1) F(n)  |
	 * |1 0|   = |F(n)   F(n-1)|
	 */
	private static BigInteger matrix(int n) {
		BigInteger[] matrix = {BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO};
		return matrixPower(matrix, n)[1];
	}
	

	/*
	 * Simple dynamic programming method. Builds up the result from the bottom up, starting with
	 * the initial seed values F(0) = 0, F(1) = 1, and applying the successor function:
	 * 	F(n) = F(n-1) + F(n-2)
	 */
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
	
	/*
	 * Simple application of the recursive definition of the Fibonacci numbers:
	 *  F(n) = F(n-1) + F(n-2)
	 */
	private static BigInteger recursive(int n) {
		if (n==0 || n==1) return BigInteger.valueOf(n);
		return recursive(n-1).add(recursive(n-2));	
	}
	
	
	/* --- Helper methods --- */
	
	private static BigInteger[] matrixMultiply(BigInteger[] a, BigInteger[] b) {
		return new BigInteger[] {
				mult(a[0], b[0]).add(mult(a[1], b[2])),
				mult(a[0], b[1]).add(mult(a[1], b[3])),
				mult(a[2], b[0]).add(mult(a[3], b[2])),
				mult(a[2], b[1]).add(mult(a[3], b[3]))
		};
	}
	
	private static BigInteger[] matrixPower(BigInteger[] b, int e) {
		if (e < 0) throw new IllegalArgumentException("Negative exponents not supported");
		
		//Begin with the identity matrix
		BigInteger[] out = {BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE};
		
		//Exponentiation by repeated squaring
		while (e != 0) {
			if (e % 2 != 0) out = matrixMultiply(out, b);
			
			e /= 2;
			b = matrixMultiply(b,b);
		}
		return out;
	}
	
	//Will be replaced with Karatsuba multiplication eventually...
	private static BigInteger mult(BigInteger a, BigInteger b) {
		return a.multiply(b);
	}
}