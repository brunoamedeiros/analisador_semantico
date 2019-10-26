package semantico;

import tabelaSimbolos.Category;
import tabelaSimbolos.Element;
import tabelaSimbolos.Hashtable;
import tabelaSimbolos.Prime;

public class AnalisadorSemantico {

	static Integer ARR_SIZE;
	static Hashtable hTable;
	static Category tipoIdentificador;
	static int numeroVariable;
	static int numeroParameter;
	static int deslocamento;
	static boolean hasParameter = false;
	static int nivel = 0;
	static Element constant;
	static Element element114;
	static AreaInstrucoes AI;
	static AreaLiterais AL;
	static MaquinaHipotetica maquinaHipotetica = new MaquinaHipotetica();

	public static void run(int X, Token token) {
		int action = X - ParserConstants.FIRST_SEMANTIC_ACTION;

		switch (action) {
		
		// #100 - Reconhecendo o nome do programa
		case 100:
			ARR_SIZE = Prime.nextPrime(30000);
			hTable = new Hashtable(ARR_SIZE);
			AI = new AreaInstrucoes();
			maquinaHipotetica.InicializaAI(AI);
			AL = new AreaLiterais();
			maquinaHipotetica.InicializaAL(AL);
			deslocamento = 3;
			numeroVariable = 0;
			numeroParameter = 0;
				
			break;

		// #104 - Encontrado o nome de rótulo, de variável, ou de parâmetro de procedure
		// em declaração
		case 104:
			switch (tipoIdentificador) {
			case VARIABLE:
				
				action104(token, tipoIdentificador, nivel, deslocamento, -1);
				incrementNumberVariable();
				incrementDeslocamento();

				break;
			case PARAMETER:
				
				action104(token, tipoIdentificador, nivel, deslocamento, -1);
				incrementDeslocamento();
				incrementNumberParameter();

				break;
			default:
				break;
			}

			break;

		// #105 - Reconhecido nome de constante em declaração s
		case 105:
			constant = new Element(token.getLexeme(), Category.CONSTANT, nivel, null, -1);

			if (hTable.objExists(constant)) {
				throw new Error("Nome de constante repetido! :(");
			} else {
				hTable.put(constant);
			}
			break;

		// #106 - Reconhecido valor de constante em declaração
		case 106:
			Integer atribute = Integer.parseInt(token.getLexeme());
			constant.setAllA(atribute);

			break;

		// #107 - Antes de lista de identificadores em declaração de variáveis
		case 107:
			tipoIdentificador = Category.VARIABLE;
			
			break;

		// #108 - Após nome de procedure, em declaração
		case 108:
			tipoIdentificador = Category.PROCEDURE;

			if (hTable.objExists(token.getLexeme())) {
				throw new Error("Nome de " + tipoIdentificador + " repetido! :(");
			} else {
				Element procedure = new Element(token.getLexeme(), tipoIdentificador, nivel, null, null);
				hTable.put(procedure);
			}
			
			hasParameter = false;
			numeroParameter = 0;
			nivel++;

			break;

		// #111 - Antes de parâmetros formais de procedures
		case 111:
			tipoIdentificador = Category.PARAMETER;
			hasParameter = true;
			
			break;

		// #114 - Atribuição parte esquerda
		case 114:
			String name = token.getLexeme();

			if (hTable.objExists(name)) {
				element114 = hTable.get(name);
				
				if (!element114.getCategoria().equals(Category.VARIABLE)) {
					throw new Error("Identificador " + element114.getName() + "não é uma váriavel, é uma " + element114.getCategoria());
				}
			} else {
				throw new Error("Identificador " + token.getLexeme() + "não está declarado! :(");
			}

			break;

		// #115 - Após expressão em atribuição
		case 115:
			
			
			maquinaHipotetica.IncluirAI(AI, 4, 0, 0);
			
			break;
			
		// #154 - Expressão – inteiro
		case 154:
			
			
			
		default:
			break;
		}
		
		hTable.showAll();

	}


	private static void action104(Token token, Category tipoIdentificador, int nivel, int geralA, int geralB) {

		if (hTable.objExists(token.getLexeme())) {
			throw new Error("Nome de " + tipoIdentificador + " repetido! :(");
		} else {
			Element element = new Element(token.getLexeme(), tipoIdentificador, nivel, geralA, geralB);
			hTable.put(element);
		}
	}
	

	private static void incrementDeslocamento() {
		deslocamento++;		
	}

	private static void incrementNumberVariable() {
		numeroVariable++;
	}

	private static void incrementNumberParameter() {
		numeroParameter++;
	}

}
