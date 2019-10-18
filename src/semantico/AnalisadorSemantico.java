package semantico;

import tabelaSimbolos.Category;
import tabelaSimbolos.Hashtable;
import tabelaSimbolos.Prime;

public class AnalisadorSemantico {

	static Integer ARR_SIZE = Prime.nextPrime(30000);
	static Hashtable hTable = new Hashtable(ARR_SIZE);
	static Category tipoIdentificador;

	public static void run(int X, Token token) {

		int action = X - ParserConstants.FIRST_SEMANTIC_ACTION;

		switch (action) {

		case 104:
//			switch (tipoIdentificador) {
//			case VARIABLE:
//				if (hTable.objExists()) {
//					System.out.println("variavel com mesmo nome :(");
//				}
//				break;
//			case PARAMETER:
//				break;
//
//			default:
//				break;
//			}

		// #107 - Antes de lista de identificadores em declaração de variáveis
		case 107:
			tipoIdentificador = Category.VARIABLE;

		default:
			break;
		}

	}

}
