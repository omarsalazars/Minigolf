package pruebadyn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Score extends JFrame{
    
    JTable table;
    JFrame frame;
    int ganador;
    
    public Score(int[][] score){
        frame=this;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(1050,400));
        DefaultTableModel model=new DefaultTableModel();
        Object[] cols=new Object[20];
        cols[0]="Hoyo";
        for(int i=1;i<19;i++)
            cols[i]=i;
        cols[19]="TOTAL";
        model.setColumnIdentifiers(cols);
        int mayor=0;
        for(int i=0;i<score.length;i++)
        {
            Object[] row=new Object[20];
            row[0]="Jugador "+i;
            int s=0;
            for(int j=0;j<score[i].length;j++)
            {
                row[j+1]=score[i][j];
                s+=score[i][j];
            }
            if(s>mayor)
            {
                ganador=i;
                mayor=s;
            }
            row[19]=s;
            model.addRow(row);
        }
        table=new JTable();
        table.getTableHeader().setBackground(Color.red);
        //table.getTableHeader().setForeground(Color.yellow);
        table.setModel(model);
        JScrollPane sp=new JScrollPane(table);
        sp.setBounds(0, 0, 1050, 200);
        this.add(sp);
        
        JButton buttonAccept=new JButton("Aceptar");
        buttonAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                if(Level.id<18)
                    new PruebaDYN().run();
                else
                {
                    JOptionPane.showMessageDialog(null,"Ganador: Jugador "+ganador);
                    Level.id=0;
                    new StartFrame();
                }
            }
        });
        buttonAccept.setBounds(450,210,550,230);
        buttonAccept.setSize(100, 100);
        this.add(buttonAccept);
        this.setLayout(null);
        this.pack();
        this.setVisible(true);
    }
}