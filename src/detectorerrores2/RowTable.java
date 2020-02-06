package detectorerrores2;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 *
 * @author Fundamentos de computación
 */
public class RowTable extends JTable
{
     Map<Integer, Color> rowColor = new HashMap<>();
     private static final long serialVersionUID = 1234567891;
     @SuppressWarnings("serial")
     public RowTable(TableModel model)
     {
          super(model);
     }

     @Override
     public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
     {
          Component c = super.prepareRenderer(renderer, row, column);

          if (!isRowSelected(row))
          {
               Color color = rowColor.get( row );
               c.setBackground(color == null ? getBackground() : color);
          }

          return c;
     }

     public void setRowColor(int row, Color color)
     {
          rowColor.put(row, color);
     }
}