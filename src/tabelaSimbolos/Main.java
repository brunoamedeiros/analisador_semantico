package tabelaSimbolos;

public class Main {

	static Integer ARR_SIZE = Prime.nextPrime(10);
	static Hashtable hTable = new Hashtable(ARR_SIZE);

	public static void main(String[] args) {

		// Inicia a tabela
		Element element1 = new Element("var1", Category.VARIABLE, 0, 0, null);
		Element element2 = new Element("var2", Category.VARIABLE, 1, 0, null);
		Element element3 = new Element("var3", Category.VARIABLE, 1, 0, null);
		Element element4 = new Element("abc", Category.CONSTANT, 0, 2, null);
		Element element5 = new Element("123", Category.CONSTANT, 0, 2, null);
		Element element6 = new Element("t1p0", Category.CONSTANT, 0, 2, null);
		Element element7 = new Element("proc1", Category.PROCEDURE, 0, 3, null);
		Element element8 = new Element("proc2", Category.PROCEDURE, 0, 3, null);
		Element element9 = new Element("par1", Category.PARAMETER, 0, 0, 0);
		Element element10 = new Element("par2", Category.PARAMETER, 0, 0, 0);

		// Adiciona na tabela
		hTable.put(element1);
		hTable.put(element2);
		hTable.put(element3);
		hTable.put(element4);
		hTable.put(element5);
		hTable.put(element6);
		hTable.put(element7);
		hTable.put(element8);
		hTable.put(element9);
		hTable.put(element10);

		// Mostra a tabela
		System.out.println("Elementos iniciais:");
		hTable.showAll();

		// Alterar dados de 5 elementos
		// Troca o nome do elemento 1
		hTable.getObj(element1).setCategoria(Category.PARAMETER);

		// Troca a categoria do elemento 2
		hTable.getObj(element2).setCategoria(Category.PROCEDURE);

		// Troca o nome do elemento 3
		hTable.getObj(element4).setCategoria(Category.VARIABLE);

		// Troca o nível do elemento 4
		hTable.getObj(element4).setNivel(1);

		// Troca a categoria do elemento 10
		hTable.getObj(element10).setCategoria(Category.CONSTANT);

		// Mostra a tabela
		System.out.println("Elementos após alteração:");
		hTable.showAll();

		// Deleta três elementos:
		System.out.println("Elementos após deletar os elementos 'var2', 't1p0' e 'par2':");
		hTable.delete(element2);
		hTable.delete(element6);
		hTable.delete(element10);

		// Mostra novamente a tabela
		hTable.showAll();

		// Busca um elemento inexistente
		System.out.print("Buscando o elemento 2 da tabela, que foi deletado: ");
		hTable.objExists(element2);
		System.out.println("");

		// Busca elementos existentes
		System.out.println("Buscando elementos existentes na tabela: \n");
		hTable.objExists(element1);
		hTable.objExists(element3);
		hTable.objExists(element4);
		hTable.objExists(element5);
	}
}
