package semantico;

import tabelaSimbolos.Category;
import tabelaSimbolos.Element;
import tabelaSimbolos.Hashtable;
import tabelaSimbolos.Prime;

public class AnalisadorSemantico {

	static Integer ARR_SIZE;
	static Hashtable hTable;
	static Category tipoIdentificador;
	static int numeroVariable, numeroParameter, deslocamento, nivel = 0, numeroLiteral = 0;
	static boolean hasParameter = false;
	static Element constant, element114, element129;
	static AreaInstrucoes AI;
	static AreaLiterais AL;
	static MaquinaHipotetica maquinaHipotetica = new MaquinaHipotetica();
	static String contexto, nameToken;

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

		// #101 - Final de programa - PARA
		case 101:
			maquinaHipotetica.IncluirAI(AI, 26, -1, -1);
			
			break;

		// Ap�s declara��o de vari�vel - AMEM
		case 102:
			maquinaHipotetica.IncluirAI(AI, 24, -1, deslocamento);
			
			break;

		// #104 - Encontrado o nome de r�tulo, de vari�vel, ou de par�metro de procedure
		// em declara��o
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

		// #105 - Reconhecido nome de constante em declara��o
		case 105:
			constant = new Element(token.getLexeme(), Category.CONSTANT, nivel, null, -1);

			if (hTable.objExists(constant)) {
				throw new Error("Nome de constante repetido! :(");
			} else {
				hTable.put(constant);
			}
			break;

		// #106 - Reconhecido valor de constante em declara��o
		case 106:
			Integer atribute = Integer.parseInt(token.getLexeme());
			constant.setAllA(atribute);

			break;

		// #107 - Antes de lista de identificadores em declara��o de vari�veis
		case 107:
			tipoIdentificador = Category.VARIABLE;

			break;

		// #108 - Ap�s nome de procedure, em declara��o
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

		// #111 - Antes de par�metros formais de procedures
		case 111:
			tipoIdentificador = Category.PARAMETER;
			hasParameter = true;

			break;

		// #114 - Atribui��o parte esquerda
		case 114:
			nameToken = token.getLexeme();

			if (hTable.objExists(nameToken)) {
				element114 = hTable.get(nameToken);

				if (!element114.getCategoria().equals(Category.VARIABLE)) {
					throw new Error("Identificador " + element114.getName() + " n�o � uma v�riavel, � uma "
							+ element114.getCategoria());
				}
			} else {
				throw new Error("Identificador " + token.getLexeme() + " n�o est� declarado! :(");
			}

			break;

		// #115 - Ap�s express�o em atribui��o
		case 115:
			geraARMZ(nivel, element114.getAllA());

			break;

		// #128 - Comando READLN in�cio
		case 128:
			contexto = "readln";

			break;

		// #129 - Identificador de vari�vel
		case 129:
			nameToken = token.getLexeme();

			if (hTable.objExists(nameToken)) {
				element129 = hTable.get(nameToken);

				switch (contexto) {
				case "express�o":

					if (element129.getCategoria().equals(Category.PROCEDURE)) {
						throw new Error("Identificador " + element114.getName() + " n�o � uma v�riavel, � uma "
								+ element129.getCategoria());
					} else if (element129.getCategoria().equals(Category.CONSTANT)) {
						
						String lexeme = token.getLexeme();
						char c = lexeme.charAt(0);
						
						int valueCRCT;
						
						if(Character.isLetter(c)) {
							valueCRCT = element129.getAllA();
						} else {
							valueCRCT = Integer.parseInt(lexeme);
						}
						
						geraCRCT(valueCRCT);
					} else {
						geraCRVL(nivel, element129.getAllA()); // CRVL
					}
					
					break;

				case "readln":
					if (!element129.getCategoria().equals(Category.VARIABLE)) {
						throw new Error("Identificador " + element114.getName() + " n�o � uma v�riavel, � uma "
								+ element129.getCategoria());
					} else {
						maquinaHipotetica.IncluirAI(AI, 21, -1, -1); // LEIT
						geraARMZ(nivel, element129.getAllA());
					}
					
					break;

				default:
					throw new IllegalArgumentException("Unexpected value: " + contexto);
				}

			} else {
				throw new Error("Identificador " + token.getLexeme() + "n�o est� declarado! :(");
			}

			break;
		
		// #130 - WRITELN ap�s express�o - IMPRL
		case 130:
//			armazena cadeia literal na �rea de literais (pega o literal identificado pelo l�xico e transposta para �rea de literais � �rea_literais) 
			maquinaHipotetica.IncluirAL(AL, token.getLexeme());
			
//			gera IMPRLIT tendo como par�metro o numero de ordem do literal 
			maquinaHipotetica.IncluirAI(AI, 23, -1, numeroLiteral);
			
//			incrementa no. de ordem do literal 
			numeroLiteral++;
			
			break;
			
		// #131 - WRITELN ap�s express�o - IMPR
		case 131:
			maquinaHipotetica.IncluirAI(AI, 22, -1, -1);
			break;

		// #141 - CMIG : compara igual
		case 141:
			maquinaHipotetica.IncluirAI(AI, 15, -1, -1);

			break;

		// #142 - CMME : compara menor
		case 142:
			maquinaHipotetica.IncluirAI(AI, 13, -1, -1);

			break;

		// #143 - CMMA : compara maior
		case 143:
			maquinaHipotetica.IncluirAI(AI, 14, -1, -1);

			break;

		// #144 - CMAI : compara maior igual
		case 144:
			maquinaHipotetica.IncluirAI(AI, 18, -1, -1);

			break;

		// #145 - CMEI : compara menor igual
		case 145:
			maquinaHipotetica.IncluirAI(AI, 17, -1, -1);

			break;

		// #146 - CMDF : compara diferente
		case 146:
			maquinaHipotetica.IncluirAI(AI, 16, -1, -1);

			break;

		// #147 - INVR : inverte sinal
		case 147:
			maquinaHipotetica.IncluirAI(AI, 9, -1, -1);

			break;

		// #148 - Express�o � soma - SOMA
		case 148:
			maquinaHipotetica.IncluirAI(AI, 5, -1, -1);

			break;

		// #149 - Express�o � subtra��o - SUBT
		case 149:
			maquinaHipotetica.IncluirAI(AI, 6, -1, -1);

			break;

		// #150 - Express�o � or - DISJ
		case 150:
			maquinaHipotetica.IncluirAI(AI, 6, -1, -1);

			break;

		// #151 - Express�o � multiplica��o - MULT
		case 151:
			maquinaHipotetica.IncluirAI(AI, 7, -1, -1);

			break;

		// #152 - Express�o � divis�o
		case 152:
			maquinaHipotetica.IncluirAI(AI, 8, -1, -1);

			break;

		// #153 - Express�o � and - CONJ
		case 153:
			maquinaHipotetica.IncluirAI(AI, 11, -1, -1);

			break;

		// #154 - Express�o � inteiro - CRCT
		case 154:
			int value = Integer.parseInt(token.getLexeme());
			geraCRCT(value);

			break;

		// #155 - Express�o � not - NOT
		case 155:
			maquinaHipotetica.IncluirAI(AI, 10, -1, -1);

			break;

		// #156 - Express�o � vari�vel
		case 156:
			contexto = "express�o";

			break;

		default:
			throw new IllegalArgumentException("Unexpected value: " + action);
		}

//		hTable.showAll();

	}

	private static void action104(Token token, Category tipoIdentificador, int nivel, int geralA, int geralB) {

		if (hTable.objExists(token.getLexeme())) {
			throw new Error("Nome de " + tipoIdentificador + " repetido! :(");
		} else {
			Element element = new Element(token.getLexeme(), tipoIdentificador, nivel, geralA, geralB);
			hTable.put(element);
		}
	}

	public static void show() {
		for (Tipos instrucoes : AI.AI) {
			System.out.println(instrucoes.codigo + " |  " + instrucoes.op1 + "  |  " + instrucoes.op2);
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

	private static void geraCRCT(int value) {
		maquinaHipotetica.IncluirAI(AI, 3, -1, value);
	}

	private static void geraCRVL(int nivel, Integer geralA) {
		maquinaHipotetica.IncluirAI(AI, 2, nivel, geralA);
	}

	private static void geraARMZ(int nivel, Integer geralA) {

//		int diffNivel = nivel - ?;

		maquinaHipotetica.IncluirAI(AI, 4, nivel, geralA);
	}
}
