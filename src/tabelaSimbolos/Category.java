package tabelaSimbolos;

public enum Category {
	VARIABLE(1), CONSTANT(2), PROCEDURE(3), PARAMETER(4);
	
    private final Integer value;
    
    private Category(Integer value){
        this.value = value;
    }
    
    public Integer getValor(){
        return value;
    }
    
}
