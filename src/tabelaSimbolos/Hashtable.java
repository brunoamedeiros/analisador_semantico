package tabelaSimbolos;

import java.util.LinkedList;

public class Hashtable {

	public LinkedList<Element>[] arr;
	public int ARR_SIZE;

	public Hashtable(int ARR_SIZE) {
		this.ARR_SIZE = ARR_SIZE;

		this.arr = new LinkedList[this.ARR_SIZE];
	}

	public Element getObj(Element element) {
		if (element == null)
			return null;

		int index = Hash.hash(element.getName(), this.ARR_SIZE);
		LinkedList<Element> items = this.arr[index];

		if (items == null)
			return null;

		for (Element item : items) {
			if (item.equals(element))
				return item;
		}

		return null;
	}

	public Element get(Element element) {
		Element item = getObj(element);

		return item;
	}

	public boolean objExists(Element element) {
		if (getObj(element) != null) {
			System.out.println("Elemento encontrado, dados: ");
			System.out.println("Nome: "+element.getName()+" Categoria: "+element.getCategoria()+
					" Nível: "+element.getNivel()+" Geral A: "+element.getAllA()+" Geral B: "+element.getAllB() + "\n");
			return true;
		}
		
		System.out.println("Elemento inexistente na tabela!");
		return false;
	}

	public void showAll() {

		
		String leftAlignFormat = "| %-9s | %-10s | %-5d | %-7d | %-7d |%n";

		System.out.format("+-----------+------------+-------+---------+---------+ %n");
		System.out.format("| Nome      | Categoria  | Nível | Geral A | Geral B | %n");
		System.out.format("+-----------+------------+-------+---------+---------+ %n");
		for (int i = 0; i < 5; i++) {
		}
		for (LinkedList<Element> item : this.arr) {
			if(item != null) {
				for (Element element : item) {
					System.out.format(leftAlignFormat, element.getName(), element.getCategoria(), element.getNivel(), element.getAllA(), element.getAllB());
				}
			}
		}
		System.out.format("+-----------+------------+-------+---------+---------+ %n\n");
	}

	public void put(Element element) {
		int index = Hash.hash(element.getName(), ARR_SIZE);
		LinkedList<Element> items = this.arr[index];

		if (items == null) {
			items = new LinkedList<Element>();

			Element item = element;
			items.add(item);

			this.arr[index] = items;
		} else {
			Element item = element;
			items.add(item);
		}
	}

	public void delete(Element element) {
		int index = Hash.hash(element.getName(), ARR_SIZE);
		LinkedList<Element> items = this.arr[index];

		if (items == null)
			return;

		for (Element item : items) {
			if (item.equals(element)) {
				items.remove(item);
				return;
			}
		}
	}

	public LinkedList<Element>[] getArr() {
		return arr;
	}

	public void setArr(LinkedList<Element>[] arr) {
		this.arr = arr;
	}

	public int getARR_SIZE() {
		return ARR_SIZE;
	}

	public void setARR_SIZE(int aRR_SIZE) {
		ARR_SIZE = aRR_SIZE;
	}
}
