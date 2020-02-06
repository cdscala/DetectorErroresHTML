/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectorerrores2;

/**
 *
 * @author Charlie
 */
public class Etiqueta {
    private int tipo;
    private String etiqueta;
    private int linea;
    private boolean valida;
    int indice;
    
    public Etiqueta(String etiqueta, int linea,int tipo){
        this.etiqueta=this.recortaEtiqueta(etiqueta);
        this.linea=linea;
        this.tipo=tipo;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public boolean isValida() {
        return valida;
    }

    public void setValida(boolean valida) {
        this.valida = valida;
    }
    
    

    private String recortaEtiqueta(String etiqueta) {
        return etiqueta.split(" ")[0];
    }

    

    
}
