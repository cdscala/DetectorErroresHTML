
package detectorerrores2;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.JFileChooser;

/**
 *
 * @author Fundamentos de computación
 */
public class Detector_errores_HTML {
    static public HashMap<String,Integer> dictionary;
    static public HashMap<String,Integer> dictionarySC;
    /**
     * @param args the command line arguments
     */
   
    
    List<Linea> leer_html(){
        
        List<Linea> leer_archivo_html = new ArrayList<>();
        try {
            JFileChooser fChooser = new JFileChooser("F:\\");//Cambiar por otro directorio 
            fChooser.showOpenDialog(null);
            File archivo = fChooser.getSelectedFile(); 
            FileInputStream fstream = new FileInputStream(archivo);
            DataInputStream in = new DataInputStream(fstream);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String linea;
                while (( linea = br.readLine()) != null) {
                    leer_archivo_html.add(new Linea(linea, linea.length()));
                }                               
            }
        } catch (IOException e) {
        }    
        
        return leer_archivo_html;
    }
    
    List<Error> publicidades (List<Linea> archivo_html){
        List<Error> errores_encontrados = new ArrayList<>();
       
        return errores_encontrados;
    }
    
    List<Error> comentarios (List<Linea> archivo_html) throws FileNotFoundException, IOException{
        //borrar
        Stack st=new Stack();
        Stack tags=new Stack();
        Stack tagsaccepted=new Stack();
        
        Stack<Etiqueta> etiquetaAbre=new Stack();
        Stack<Etiqueta> etiquetaAbreComentario=new Stack();
        Stack<Etiqueta> etiquetaCierraComentario=new Stack();
        String etiqueta="";
        chargeDictionary();
        List<Error> errores_encontrados = new ArrayList<>();
        Iterator<Linea> iterator_linea = archivo_html.iterator();
        int numero_linea=0;
        int state=0;
        while (iterator_linea.hasNext()) {
            Linea next = iterator_linea.next();
            numero_linea++;
            for (int i=0;i<next.get_largo();i++){
                //System.out.println(next.get_linea().charAt(i));
                //estado 0
                if (next.get_linea().charAt(i)=='<' && state==0){
                    System.out.println("estoy en el estado: "+state+"\n");
                    etiqueta="";
                    state=1;
                    continue;
                }
                //estado 1
                if (state==1){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch (next.get_linea().charAt(i)) {
                        case '!':
                            state=6;
                            break;
                        case '/':
                            state=4;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=2;
                            break;
                    }
                    continue;
                }
                //estado 2
                if (state==2){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '>':
                            System.out.println("Etiqueta completa: "+etiqueta);
                            Etiqueta prueba=new Etiqueta(etiqueta,numero_linea,1);//tipo 1 etiqueta con cierre
                            prueba.setValida(validaEtiqueta(prueba.getEtiqueta()));
                            prueba.setTipo(determinarTipo(prueba.getEtiqueta()));
                            if(prueba.getTipo()==1 && prueba.isValida()) {etiquetaAbre.push(prueba);System.out.println("Push: "+prueba.getEtiqueta()); System.out.println("Etiqueta valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());} 
                            else { 
                                if (prueba.getTipo()==1)errores_encontrados.add(new Error(numero_linea,"Error etiqueta "+prueba.getEtiqueta()+" no valida"));
                                System.out.println("Etiqueta no valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());
                            }
                            state=0;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=2;
                            break;
                    }
                    continue;
                }
                //estado 4
                if (state==4){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '>':
                            System.out.println("Etiqueta completa: "+etiqueta);
                            Etiqueta prueba=new Etiqueta(etiqueta,numero_linea,1);//tipo 1 etiqueta que abre
                            prueba.setValida(validaEtiqueta(prueba.getEtiqueta()));
                            prueba.setTipo(determinarTipo(prueba.getEtiqueta()));
                            if(prueba.isValida()) {
                                if(!etiquetaAbre.empty()){//etiqueta que abre no vacia
                                    if(prueba.getEtiqueta().equals(etiquetaAbre.peek().getEtiqueta())){ 
                                        if(!etiquetaCierraComentario.empty()) {
                                            if(!etiquetaAbre.peek().getEtiqueta().equals(etiquetaCierraComentario.peek().getEtiqueta())){
                                                System.out.println("pop 1: "+ etiquetaAbre.peek().getEtiqueta());
                                                etiquetaAbre.pop();
                                            }
                                            else{
                                                System.out.println("pop 2: "+ etiquetaAbre.peek().getEtiqueta());
                                                etiquetaAbre.pop();
                                            }
                                        }
                                        else {
                                            System.out.println("pop 3: "+ etiquetaAbre.peek().getEtiqueta());
                                            etiquetaAbre.pop();
                                        }
                                    }
                                    else{
                                        if(!etiquetaCierraComentario.empty()) {
                                            if(etiquetaAbre.peek().getEtiqueta().equals(etiquetaCierraComentario.peek().getEtiqueta())){
                                                System.out.println("pop 4: "+ etiquetaCierraComentario.peek().getEtiqueta());
                                                errores_encontrados.add(new Error(etiquetaCierraComentario.peek().getLinea(),"Error etiqueta que cierra ("+etiquetaCierraComentario.peek().getEtiqueta()+") en comentario"));
                                                etiquetaCierraComentario.pop();
                                                System.out.println("pop 5: "+ etiquetaAbre.peek().getEtiqueta());
                                                etiquetaAbre.pop();
                                                if(etiquetaAbre.peek().getEtiqueta().equals(prueba.getEtiqueta())){
                                                    System.out.println("pop 6: "+ etiquetaAbre.peek().getEtiqueta());
                                                    etiquetaAbre.pop();
                                                }
                                                else {
                                                    if(!etiquetaAbreComentario.empty()){
                                                        if(etiquetaAbreComentario.peek().getEtiqueta().equals(prueba.getEtiqueta())){
                                                            System.out.println("pop 7: "+ etiquetaAbreComentario.peek().getEtiqueta());
                                                            errores_encontrados.add(new Error(etiquetaAbreComentario.peek().getLinea(),"Error etiqueta que abre ("+etiquetaAbreComentario.peek().getEtiqueta()+") en comentario"));
                                                            etiquetaAbreComentario.pop();
                                                        }
                                                        else{
                                                            errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 1"));
                                                        }
                                                    }
                                                    else{
                                                        errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 2"));
                                                    }
                                                }
                                            }
                                            else if(!etiquetaAbreComentario.empty()){
                                                if(etiquetaAbreComentario.peek().getEtiqueta().equals(prueba.getEtiqueta())){
                                                    System.out.println("pop 7: "+ etiquetaAbreComentario.peek().getEtiqueta());
                                                    errores_encontrados.add(new Error(etiquetaAbreComentario.peek().getLinea(),"Error etiqueta que abre ("+etiquetaAbreComentario.peek().getEtiqueta()+") en comentario"));
                                                    etiquetaAbreComentario.pop();
                                                }
                                                else{
                                                    errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 3"));
                                                }
                                            }
                                            else{
                                                errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 4"));
                                            }
                                        }
                                        else if(!etiquetaAbreComentario.empty()){
                                            if(etiquetaAbreComentario.peek().getEtiqueta().equals(prueba.getEtiqueta())){
                                                System.out.println("pop 8: "+ etiquetaAbreComentario.peek().getEtiqueta());
                                                etiquetaAbreComentario.pop();
                                                errores_encontrados.add(new Error(numero_linea,"Error etiqueta que abre ("+prueba.getEtiqueta()+") en comentario"));
                                            }
                                            else{
                                                errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 5"));
                                            }
                                        }
                                        else{
                                            errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 6"));
                                        }
                                    }
                                    
                                }
                                else if(!etiquetaAbreComentario.empty()){
                                        if(etiquetaAbreComentario.peek().getEtiqueta().equals(prueba.getEtiqueta())){
                                            System.out.println("pop 9: "+ etiquetaAbreComentario.peek().getEtiqueta());
                                            etiquetaAbreComentario.pop();
                                            errores_encontrados.add(new Error(numero_linea,"Error etiqueta que abre ("+prueba.getEtiqueta()+") en comentario"));
                                        }
                                        else{
                                            errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 7"));
                                        }
                                    }
                                
                                else{
                                    errores_encontrados.add(new Error(numero_linea,"Error etiqueta ("+prueba.getEtiqueta()+") sin apertura 8"));
                                }
                            }
                            else { 
                                System.out.println("Etiqueta no valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());
                                errores_encontrados.add(new Error(numero_linea,"Error etiqueta "+prueba.getEtiqueta()+" no valida"));
                            }
                            state=0;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=4;
                            break;
                    }
                    
                    continue;
                }
                
                //estado 6
                if (state==6){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '-':
                            state=7;
                            break;
                        default:
                            state=0;
                            break;
                    }
                    continue;
                }
                //estado 7
                if (state==7){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '-':
                            state=8;
                            break;
                        default:
                            state=0;
                            break;
                    }
                    continue;
                }
                //estado 8
                if (state==8){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '-':
                            state=10;
                            break;
                        case '<':
                            etiqueta="";
                            state=9;
                            break;
                    }
                    continue;
                }
                //estado 9
                if (state==9){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '-':
                            state=10;
                            break;
                        case '>':
                            state=8;
                            break;
                        case '/':
                            state=13;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=12;
                            break;
                    }
                    continue;
                }
                //estado 10
                if (state==10){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '-':
                            state=11;
                            break;
                        case '<':
                            state=9;
                            break;
                        default:
                            state=8;
                            break;
                    }
                    continue;
                }
                //estado 11
                if (state==11){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '>':
                            state=0;
                            break;
                        case '<':
                            state=9;
                            break;
                        default:
                            state=8;
                            break;
                    }
                    continue;
                }
                //estado 12
                if (state==12){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '<':
                            state=9;
                            break;
                        case '-':
                            state=10;
                            break;
                        case '>':
                            System.out.println("Etiqueta completa: "+etiqueta);
                            Etiqueta prueba=new Etiqueta(etiqueta,numero_linea,1);//tipo 1 etiqueta que abre
                            prueba.setValida(validaEtiqueta(prueba.getEtiqueta()));
                            prueba.setTipo(determinarTipo(prueba.getEtiqueta()));
                            if(prueba.getTipo()==1 && prueba.isValida()) {etiquetaAbreComentario.push(prueba);System.out.println("Push abre comentario: "+prueba.getEtiqueta()); System.out.println("Etiqueta valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());}
                            else { System.out.println("Etiqueta no valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());}
                            state=8;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=12;
                            break;
                    }
                    continue;
                }
                //estado 13
                if (state==13){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '<':
                            state=9;
                            break;
                        case '-':
                            state=10;
                            break;
                        case '>':
                            state=8;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=14;
                            break;
                    }
                    continue;
                }
                //estado 14
                if (state==14){
                    System.out.println("estoy en el estado: "+state+"\n");
                    switch(next.get_linea().charAt(i)){
                        case '<':
                            state=9;
                            break;
                        case '-':
                            state=10;
                            break;
                        case '>':
                            System.out.println("Etiqueta completa: "+etiqueta);
                            Etiqueta prueba=new Etiqueta(etiqueta,numero_linea,1);//tipo 1 etiqueta que abre
                            prueba.setValida(validaEtiqueta(prueba.getEtiqueta()));
                            prueba.setTipo(determinarTipo(prueba.getEtiqueta()));
                            if(prueba.isValida() && etiquetaAbre.peek().getEtiqueta().equals(prueba.getEtiqueta())) {etiquetaCierraComentario.push(prueba);System.out.println("Push cierra comentario: "+prueba.getEtiqueta()); System.out.println("Etiqueta valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());}
                            else { System.out.println("Etiqueta no valida: "+prueba.getEtiqueta() +" linea: "+prueba.getLinea()+" tipo: "+prueba.getTipo());}
                            state=8;
                            break;
                        default:
                            etiqueta=etiqueta+next.get_linea().charAt(i);
                            state=14;
                            break;
                    }
                    continue;
                }
            }
            
        }
        System.out.println("pila etiquetas que abren: ");
        while(!etiquetaAbre.empty()){
            System.out.println(etiquetaAbre.pop().getEtiqueta());
        }
        System.out.println("pila etiquetas que abren en comentario: ");
        while(!etiquetaAbreComentario.empty()){
            System.out.println(etiquetaAbreComentario.pop().getEtiqueta());
        }
        
        System.out.println("pila etiquetas que cierran en comentario: ");
        while(!etiquetaAbreComentario.empty()){
            System.out.println(etiquetaCierraComentario.pop().getEtiqueta());
        }
        
        return errores_encontrados;
    }
    
    List<Error> estructura_codigo (List<Linea> archivo_html){
        List<Error> errores_encontrados = new ArrayList<>();
        
        return errores_encontrados;
    }
    
    List<Error> imagenes (List<Linea> archivo_html){
        List<Error> errores_encontrados = new ArrayList<>();
        
        return errores_encontrados;
    }
    
    List<Error> validacion_formularios (List<Linea> archivo_html){
        List<Error> errores_encontrados = new ArrayList<>();
        
        return errores_encontrados;
    }    

    private void chargeDictionary() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("src\\detectorerrores2\\input.txt")));
        String inputLine = null;
        int value=1;
        dictionary = new HashMap<>();
        dictionarySC= new HashMap<>();
        //carga archivo
        while((inputLine = reader.readLine()) != null) {
            // Separar palabras del archivo de texto.
            String[] words = inputLine.split("\\s+");
            // Ignorar lineas vacias.
            if(inputLine.equals(""))
                continue;
            for(String word: words) {
                // descartar comas y puntos.
                word = word.replace(".", "");
                word = word.replace(",", "");
                 
                if(dictionary.containsKey(word)) {
                    Integer val = dictionary.get(word);
                    dictionary.put(word, val + 1);
                }
                else
                    dictionary.put(word, value++);
            }
            
        }
        reader = new BufferedReader(new FileReader(new File("src\\detectorerrores2\\input2.txt")));
        inputLine = null;
        value=1;
        while((inputLine = reader.readLine()) != null) {
            // Separar palabras del archivo de texto.
            String[] words = inputLine.split("\\s+");
            // Ignorar lineas vacias.
            if(inputLine.equals(""))
                continue;
            for(String word: words) {
                // descartar comas y puntos.
                word = word.replace(".", "");
                word = word.replace(",", "");
                 
                if(dictionarySC.containsKey(word)) {
                    Integer val = dictionarySC.get(word);
                    dictionarySC.put(word, val + 1);
                }
                else
                    dictionarySC.put(word, value++);
            }
            
        }
    }
    
    private boolean validaEtiqueta(String etiqueta) {
        return dictionary.containsKey(etiqueta);
    }
    
    private int obtenerIndice(String etiqueta) {
        if(dictionary.get(etiqueta)!=null)return dictionary.get(etiqueta);
        else return 0;
    }

    private int determinarTipo(String etiqueta) {//1 para etiquetas comunes 2 para etiquetas sin cierre
        if(dictionarySC.containsKey(etiqueta)){return 2;}
        else return 1;
    }

}
