package detectorerrores2;
/**
 *
 * @author Fundamentos de computación
 */
public class Linea {
    private String caracteres;
    private int largo;
    Linea(String linea, int contador) {
        caracteres = linea;
        largo = contador;
    }
    
    public String get_linea(){
        return caracteres;
    }
    
    public void set_linea(String l){
    	caracteres = l;
    }
    public int get_largo(){
        return largo;
    }
    
    public void set_largo(int l){
    	largo = l;
    }
}
