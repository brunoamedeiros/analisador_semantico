package tabelaSimbolos;

import java.math.BigInteger;

public class Prime {

	// Function to get nextPrimeNumber
	public static int nextPrime(Integer n) {
		BigInteger b = new BigInteger(String.valueOf(n));
		return Integer.parseInt(b.nextProbablePrime().toString());
	}
}