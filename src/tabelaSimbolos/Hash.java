package tabelaSimbolos;

public class Hash {

	static int hash(String key, long tableSize) {
		int hashVal = 0; // uses Horner�s method to evaluate a polynomial
		
		for (int i = 0; i < key.length(); i++)
			hashVal = 37 * hashVal + key.charAt(i);
		
		hashVal %= tableSize;

		if (hashVal < 0)
			hashVal += tableSize; // needed if hashVal is negative
		
		return hashVal;
	}
}