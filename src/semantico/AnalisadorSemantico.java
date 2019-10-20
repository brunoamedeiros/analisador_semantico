package semantico;

import tabelaSimbolos.Category;
import tabelaSimbolos.Element;
import tabelaSimbolos.Hashtable;
import tabelaSimbolos.Prime;

public class AnalisadorSemantico {

	static Integer ARR_SIZE = Prime.nextPrime(30000);
	static Hashtable hTable = new Hashtable(ARR_SIZE);
	static Category tipoIdentificador;
	static int nivel = 0;

	public static void run(int X, Token token) {
		int action = X - ParserConstants.FIRST_SEMANTIC_ACTION;
		
		System.out.println(token);

		switch (action) {

		case 104:
			switch (tipoIdentificador) {
			case VARIABLE:

				Element element = new Element(token.getLexeme(), tipoIdentificador, nivel, 0, 0);
				
				if (hTable.objExists(element)) {
					System.out.println("variavel com mesmo nome :(");
				} else {
					hTable.put(element);
					nivel++;
					System.out.println(element);
				}
				break;
			case PARAMETER:
				break;

			default:
				break;
			}

			// #107 - Antes de lista de identificadores em declaração de variáveis
		case 107:
			tipoIdentificador = Category.VARIABLE;
			break;

		default:
			break;
		}

	}

}
