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