package semantico;

import java.util.ArrayList;
import java.util.Stack;

import tabelaSimbolos.Category;
import tabelaSimbolos.Element;
import tabelaSimbolos.Hashtable;
import tabelaSimbolos.Prime;

public class AnalisadorSemantico {

	static Integer ARR_SIZE;
	static Hashtable hTable;
	static Category tipoIdentificador;
	static int numeroVariable, numeroParameter, deslocamento, deslocamentoParameter, nivel = 0, numeroLiteral = 0,
			valueCall, value139, numeroParameterEfetivos, valueRepeat, value132;
	static boolean hasParameter = false;
	static Element constant, element114, element116, element129, procedure, element137;
	static AreaInstrucoes AI;
	static AreaLiterais AL;
	static MaquinaHipotetica maquinaHipotetica = new MaquinaHipotetica();
	static String contexto, nameToken;
	static Stack<Integer> desviosDSVS = new Stack<Integer>(), desviosDSVF = new Stack<Integer>(),
			desviosDSVSiF = new Stack<Integer>(), desviosDSVSRepeat = new Stack<Integer>(),
			desviosDSVSCase = new Stack<Integer>(), desviosDSVT = new Stack<Integer>(),
			integerCase = new Stack<Integer>();
	static Stack<String> parametersProcedure = new Stack<String>();
	static Stack<Element> procedures = new Stack<Element>();

	public static void geraInstrucoes(int X, Token token) {
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
			deslocamentoParameter = 0;
			numeroVariable = 0;
			numeroParameter = 0;
			numeroParameterEfetivos = 0;
			numeroLiteral = 0;
			desviosDSVF.removeAllElements();
			desviosDSVS.removeAllElements();
			desviosDSVSiF.removeAllElements();
			desviosDSVSCase.removeAllElements();
			parametersProcedure.removeAllElements();
			procedures.removeAllElements();
			integerCase.removeAllElements();
//			AI.LC = 1;
//			AL.LIT = 1;

			break;

		// #101 - Final de programa - PARA
		case 101:
			maquinaHipotetica.IncluirAI(AI, 26, -1, -1);

			break;

		// #102 - Ap�s declara��o de vari�vel - AMEM
		case 102:
			geraAMEM(deslocamento);
			deslocamento = 3;

			break;

		// #104 - Encontrado o nome de r�tulo, de vari�vel, ou de par�metro de procedure
		// em declara��o
		case 104:
			switch (tipoIdentificador) {
			case VARIABLE:

				action104(token, tipoIdentificador, nivel, deslocamento, -1);
				incrementDeslocamento();
				incrementNumberVariable();

				break;
			case PARAMETER:
				parametersProcedure.push(token.getLexeme());
				action104(token, tipoIdentificador, nivel, 0, -1);
				incrementDeslocamentoParameter();
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
				procedure = new Element(token.getLexeme(), tipoIdentificador, nivel, AI.LC + 1, 0);
				hTable.put(procedure);
				procedures.push(procedure);
			}

			hasParameter = false;
			numeroParameter = 0;
			deslocamentoParameter = 0;
			parametersProcedure.removeAllElements();
			nivel++;

			break;

		// #109 - Ap�s declara��o de procedure
		case 109:
			if (hasParameter) {
				procedure.setAllB(numeroParameter);

				// preenche atributos dos par�metros (deslocamento):
				// primeiro par�metro �> deslocamento = - (np)
				// segundo par�metro �> deslocamento = - (np � 1)
				for (int i = 1; i <= procedure.getAllB(); i++) {
					hTable.getObj(parametersProcedure.pop(), nivel).setAllA((-i));
				}
			}

			geraDSVS();

			break;

		// #110 - Fim de procedure
		case 110:
			int instrucao = AI.LC + 1;
			alteraDesvio(desviosDSVS.pop(), instrucao);

			maquinaHipotetica.IncluirAI(AI, 1, -1, procedures.pop().getAllB()); // RETU

			nivel--;

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
			geraARMZ(nivel, element114);

			break;

		// #116 - Chamada de procedure - CALL
		case 116:
			nameToken = token.getLexeme();

			if (hTable.objExists(nameToken)) {
				element116 = hTable.get(nameToken);

				if (!element116.getCategoria().equals(Category.PROCEDURE)) {
					throw new Error("Identificador " + element116.getName() + " n�o � uma procedure, � uma "
							+ element116.getCategoria());
				} else {
					valueCall = element116.getAllA();
				}
			} else {
				throw new Error("Identificador " + token.getLexeme() + " n�o est� declarado! :(");
			}

			break;

		// #117 - Ap�s comando call
		case 117:
			if (element116.getAllB() != numeroParameterEfetivos) {
				throw new Error("Est� faltando instanciar parametros para a procedure " + element116.getName());
			} else {

				int diffNivel = nivel - element116.getNivel();

				maquinaHipotetica.IncluirAI(AI, 25, diffNivel, valueCall);
			}

			numeroParameterEfetivos = 0;

			break;

		// #118 - Ap�s express�o, em comando call
		case 118:
			numeroParameterEfetivos++;

			break;

		// #120 - Ap�s express�o num comando IF
		case 120:
			geraDSVF();

			break;

		// #121 - Ap�s instru��o IF
		case 121:

			int instrucao121 = AI.LC;
			if (desviosDSVSiF.size() > 0) {
				alteraDesvio(desviosDSVSiF.pop(), instrucao121);
			}

			break;

		// #122 - Ap�s dom�nio do THEN, antes do ELSE
		case 122:
			int instrucao122 = AI.LC + 1;
			alteraDesvio(desviosDSVF.pop(), instrucao122);

			geraDSVSiF();

			break;

		// #157 - Instru��o nova para resolver o problema quando existe um IF sem ELSE,
		// modifica��o feita no ParserConstants.java:76
		case 157:
			int instrucao157 = AI.LC;
			alteraDesvio(desviosDSVF.pop(), instrucao157);
			break;

		// #123 - Comando WHILE antes da express�o
		case 123:
			desviosDSVS.push(AI.LC);

			break;

		// #124 - Comando WHILE depois da express�o
		case 124:
			geraDSVF();

			break;

		// #125 - Ap�s comando WHILE
		case 125:
			int instrucao125DSVF = AI.LC + 1;
			alteraDesvio(desviosDSVF.pop(), instrucao125DSVF);
			geraDSVS(desviosDSVS.pop());

			break;

		// #126 - Comando REPEAT � in�cio
		case 126:
			desviosDSVSRepeat.push(AI.LC);

			break;

		// #127 - Comando REPEAT � fim
		case 127:
			geraDSVF(desviosDSVSRepeat.pop());

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
						throw new Error("Identificador " + element129.getName() + " n�o � uma v�riavel, � uma "
								+ element129.getCategoria());
					} else if (element129.getCategoria().equals(Category.CONSTANT)) {

						String lexeme = token.getLexeme();
						char c = lexeme.charAt(0);

						int valueCRCT;

						if (Character.isLetter(c)) {
							valueCRCT = element129.getAllA();
						} else {
							valueCRCT = Integer.parseInt(lexeme);
						}

						geraCRCT(valueCRCT);
					} else {
						geraCRVL(nivel, element129); // CRVL
					}

					break;

				case "readln":
					if (!element129.getCategoria().equals(Category.VARIABLE)) {
						throw new Error("Identificador " + element129.getName() + " n�o � uma v�riavel, � uma "
								+ element129.getCategoria());
					} else {
						maquinaHipotetica.IncluirAI(AI, 21, -1, -1); // LEIT
						geraARMZ(nivel, element129);
					}

					break;

				default:
					throw new IllegalArgumentException("Unexpected value: " + contexto);
				}

			} else {
				throw new Error("Identificador " + token.getLexeme() + " n�o est� declarado! :(");
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

		// #132 - Ap�s palavra reservada CASE
		case 132:
			integerCase.removeAllElements();

			break;

		// #133 - Ap�s comando CASE
		case 133:

			for (Integer desvio : desviosDSVSCase) {
				alteraDesvio(desvio, AI.LC);
			}

			desviosDSVSCase.removeAllElements();

			geraAMEM(-1);

			break;

		// #134 - Ramo do CASE ap�s inteiro, �ltimo da lista
		case 134:
			geraCOPI();
			geraCRCT(integerCase.pop());
			geraCMIG();

			if (desviosDSVT.size() > 0) {
				
				int instrucao134 = AI.LC + 1;
				for (Integer desvio : desviosDSVT) {
					alteraDesvio(desvio, instrucao134);
				}

				desviosDSVT.removeAllElements();
			}

			geraDSVF();

			break;

		// #135 - Ap�s comando em CASE
		case 135:
			int instrucao135 = AI.LC + 1;
			alteraDesvio(desviosDSVF.pop(), instrucao135);
			geraDSVSCase();

			break;

		// #136 - Ramo do CASE: ap�s inteiro
		case 136:
			geraCOPI();
			geraCRCT(integerCase.pop());
			geraCMIG();
			geraDSVT();

			break;

		// #137 - Ap�s vari�vel controle comando FOR
		case 137:
			nameToken = token.getLexeme();

			if (hTable.objExists(nameToken)) {
				element137 = hTable.get(nameToken);

				if (!element137.getCategoria().equals(Category.VARIABLE)) {
					throw new Error("Identificador " + element137.getName() + " n�o � uma v�riavel, � uma "
							+ element137.getCategoria());
				}
			} else {
				throw new Error("Identificador " + token.getLexeme() + " n�o est� declarado! :(");
			}

			break;

		// #138 - Ap�s express�o valor inicial
		case 138:
			geraARMZ(nivel, element137);

			break;

		// #139 - Ap�s express�o � valor final
		case 139:
			value139 = AI.LC;
			geraCOPI();
			geraCRVL(nivel, element137);
			geraCMAI();
			geraDSVF();

			break;

		// #140 - Ap�s comando em FOR
		case 140:
			geraCRVL(nivel, element137);
			geraCRCT(1);
			geraSOMA();
			geraARMZ(nivel, element137);

			int instrucao140 = AI.LC + 1;
			alteraDesvio(desviosDSVF.pop(), instrucao140);

			geraDSVS(value139);
			geraAMEM(-1);

			break;

		// #141 - CMIG : compara igual
		case 141:
			geraCMIG();

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
			geraCMAI();

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
			geraSOMA();

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

	public static void run() {
		maquinaHipotetica.Interpreta(AI, AL);
		maquinaHipotetica.mostraAreaDados();
	}

	private static void action104(Token token, Category tipoIdentificador, int nivel, int geralA, int geralB) {

		if (hTable.objExists(token.getLexeme(), nivel)) {
			throw new Error("Nome de " + tipoIdentificador + " repetido! :(");
		} else {
			Element element = new Element(token.getLexeme(), tipoIdentificador, nivel, geralA, geralB);
			hTable.put(element);
		}
	}

	private static void incrementDeslocamento() {
		deslocamento++;
	}

	private static void incrementDeslocamentoParameter() {
		deslocamentoParameter++;
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

	private static void geraCRVL(int nivel, Element element) {
		int diffNivel = nivel - element.getNivel();
		maquinaHipotetica.IncluirAI(AI, 2, diffNivel, element.getAllA());
	}

	private static void geraARMZ(int nivel, Element element) {

		int diffNivel = nivel - element.getNivel();

		maquinaHipotetica.IncluirAI(AI, 4, diffNivel, element.getAllA());
	}

	private static void geraSOMA() {
		maquinaHipotetica.IncluirAI(AI, 5, -1, -1);
	}

	private static void geraCMIG() {
		maquinaHipotetica.IncluirAI(AI, 15, -1, -1);
	}

	private static void geraCMAI() {
		maquinaHipotetica.IncluirAI(AI, 18, -1, -1);
	}

	private static void geraCOPI() {
		maquinaHipotetica.IncluirAI(AI, 28, -1, -1);
	}

	private static void geraDSVF() {
		desviosDSVF.push(AI.LC);
		maquinaHipotetica.IncluirAI(AI, 20, -1, 0);
	}

	private static void geraDSVF(int position) {
		maquinaHipotetica.IncluirAI(AI, 20, -1, position);
	}

	private static void alteraDesvio(int position, int instrucao) {
		maquinaHipotetica.AlterarAI(AI, position, -1, instrucao);
	}

	private static void geraDSVS() {
		desviosDSVS.push(AI.LC);
		maquinaHipotetica.IncluirAI(AI, 19, -1, 0);
	}

	private static void geraDSVS(int position) {
		maquinaHipotetica.IncluirAI(AI, 19, -1, position);
	}

	private static void geraDSVSiF() {
		desviosDSVSiF.push(AI.LC);
		maquinaHipotetica.IncluirAI(AI, 19, -1, 0);
	}

	private static void geraDSVSCase() {
		desviosDSVSCase.push(AI.LC);
		maquinaHipotetica.IncluirAI(AI, 19, -1, 0);
	}

	private static void geraDSVT() {
		desviosDSVT.push(AI.LC);
		maquinaHipotetica.IncluirAI(AI, 29, -1, 0);
	}

	private static void geraAMEM(int value) {
		maquinaHipotetica.IncluirAI(AI, 24, -1, value);
	}

	public static void addIntegerCase(int integer) {
		integerCase.add(integer);
	}

	public static ArrayList<Object[]> show() {
		String instrucao = null;
		int row = 0, op1 = 0, op2 = 0;

		ArrayList<Object[]> listaInstrucoes = new ArrayList<Object[]>();

		for (Tipos instrucoes : AI.AI) {

			switch (instrucoes.codigo) {
			case 1:

				instrucao = "RETU";
				break;

			case 2:
				instrucao = "CRVL";
				break;

			case 3:
				instrucao = "CRCT";
				break;

			case 4:
				instrucao = "ARMZ";
				break;

			case 5:
				instrucao = "SOMA";
				break;

			case 6:
				instrucao = "SUBT";
				break;

			case 7:
				instrucao = "MULT";
				break;

			case 8:
				instrucao = "DIVI";
				break;

			case 9:
				instrucao = "INVR";
				break;

			case 10:
				instrucao = "NEGA";
				break;

			case 11:
				instrucao = "CONJ";
				break;

			case 12:
				instrucao = "DISJ";
				break;

			case 13:
				instrucao = "CMME";
				break;

			case 14:
				instrucao = "CMMA";
				break;

			case 15:
				instrucao = "CMIG";
				break;

			case 16:
				instrucao = "CMDF";
				break;

			case 17:
				instrucao = "CMEI";
				break;

			case 18:
				instrucao = "CMAI";
				break;

			case 19:
				instrucao = "DSVS";
				break;

			case 20:
				instrucao = "DSVF";
				break;

			case 21:
				instrucao = "LEIT";
				break;

			case 22:
				instrucao = "IMPR";
				break;

			case 23:
				instrucao = "IMPRL";
				break;

			case 24:
				instrucao = "AMEM";
				break;

			case 25:
				instrucao = "CALL";
				break;

			case 26:
				instrucao = "PARA";
				break;

			case 27:
				instrucao = "NADA";
				break;

			case 28:
				instrucao = "COPI";
				break;

			case 29:
				instrucao = "DSVT";
				break;
			}

			op1 = instrucoes.op1;
			op2 = instrucoes.op2;

			listaInstrucoes.add(new Object[] { row, instrucao, op1, op2 });

			row++;

			if (instrucoes.codigo == 26) {
				break;
			}
		}

		return listaInstrucoes;

	}
}
