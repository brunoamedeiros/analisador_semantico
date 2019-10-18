package tabelaSimbolos;

public class Element {

	private String name;
	private Category category;
	private Integer level;
	private Integer allA;
	private Integer allB;

	public Element(String name, Category category, Integer level, Integer allA, Integer allB) {
		this.name = name;
		this.category = category;
		this.level = level;
		this.allA = allA;
		this.allB = allB;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategoria() {
		return category;
	}

	public void setCategoria(Category category) {
		this.category = category;
	}

	public Integer getNivel() {
		return level;
	}

	public void setNivel(Integer level) {
		this.level = level;
	}

	public Integer getAllA() {
		return allA;
	}

	public void setAllA(Integer allA) {
		this.allA = allA;
	}

	public Integer getAllB() {
		return allB;
	}

	public void setAllB(Integer allB) {
		this.allB = allB;
	}

	@Override
	public String toString() {
		return "Element [name=" + name + ", category=" + category + ", level=" + level + ", allA=" + allA + ", allB="
				+ allB + "]";
	}
}
