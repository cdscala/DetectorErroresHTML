
package detectorerrores2;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Fundamentos de computación
 * 
 */
public class Visualizar extends javax.swing.JFrame {
    private static final long serialVersionUID = 1234567891;
    @SuppressWarnings("serial")
    public Visualizar() {
        initComponents();
    }

    @SuppressWarnings("serial")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
                DefaultTableModel dtm = new DefaultTableModel();
        jTable1 = new RowTable(dtm);
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Línea", "Código"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Errores"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
    

    public static void ejecutar_detector() throws IOException{
        
        Detector_errores_HTML deh =  new Detector_errores_HTML();
        List<Linea> archivo_html = deh.leer_html();       
        List<Error> errores = deh.comentarios(archivo_html);
        //errores.addAll(deh.comentarios(archivo_html));
        //errores.addAll(deh.estructura_codigo(archivo_html));
        //errores.addAll(deh.imagenes(archivo_html));        
        //errores.addAll(deh.validacion_formularios(archivo_html));        

       // Ejemplo:          
       // errores.add(new Error(3,"Comentario del error en la linea 3"));
       // errores.add(new Error(2,"Comentario del error en la linea 2"));
       // errores.add(new Error(2,"Comentario del error en la linea 2 v2"));
       // errores.add(new Error(2,"Comentario del error en la linea 2 v3"));
       // errores.add(new Error(6,"Comentario del error en la linea 6"));
//        

        Collections.sort(errores, (Error e1, Error e2) -> {
            Integer i1 = e1.get_num_linea();
            Integer i2 = e2.get_num_linea();
            return i1.compareTo(i2);
        });
        
        DefaultTableModel tabla_html = new DefaultTableModel (); 
        int i = 1;
        
        tabla_html.addColumn("Línea");
        tabla_html.addColumn("Código");
        Iterator<Error> iterator_error = errores.iterator();
        Error next_error = null;
        if(iterator_error.hasNext())
            next_error = iterator_error.next();
        for (Iterator<Linea> iterator_linea = archivo_html.iterator(); iterator_linea.hasNext();i++) {
            Linea next = iterator_linea.next();
            Vector<Object> datos = new Vector<>();    
            datos.addElement(i);
            datos.addElement(next.get_linea());            
            tabla_html.addRow(datos);
            if(next_error != null && next_error.get_num_linea() == i){
                jTable1.setRowColor(i-1, Color.red);
                boolean flag = true;
                while(iterator_error.hasNext() &&  flag){
                    next_error = iterator_error.next();
                    if(next_error.get_num_linea() != i){
                        flag = false;
                    }
                }                
            }
        }
        
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.setModel(tabla_html);
        
        jTable1.setEnabled(false);
        jTable1.addMouseListener(
            new MouseInputAdapter() 
            {
               @Override
               public void mouseClicked(MouseEvent me)
               {
                    int row = jTable1.rowAtPoint(me.getPoint());
                    row++;
                    
                    DefaultTableModel dtm = new DefaultTableModel();
                    dtm.addColumn("Errores");
                   for (Error next : errores) {
                       if(next.get_num_linea() == row){
                           Vector<Object> datos = new Vector<>();
                           datos.add(next.get_comentario());
                           dtm.addRow(datos);
                       }
                   }
                     jTable2.setModel(dtm);
                  
               }
            }
         );
    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Visualizar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Visualizar().setVisible(true);
            try {
                ejecutar_detector();
            } catch (IOException ex) {
                Logger.getLogger(Visualizar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private static RowTable jTable1;
    private static javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
