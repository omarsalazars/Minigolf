package pruebadyn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class Menu extends JFrame{
    
    public Menu(){
        JFrame frame=this;
        this.setTitle("SCORE");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton buttonAccept=new JButton("Aceptar");
        JLabel labelJugadores=new JLabel("Numero de jugadores: ");
        JSpinner spinnerJugadores=new JSpinner();
        SpinnerNumberModel sm=new SpinnerNumberModel();
        sm.setMaximum(4);
        sm.setMinimum(1);
        sm.setValue(1);
        spinnerJugadores.setModel(sm);
        this.setPreferredSize(new Dimension(400,180));
        buttonAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                PruebaDYN.players=(int)sm.getValue();
                PruebaDYN simulation = new PruebaDYN();
                PruebaDYN.score=new int[PruebaDYN.players][18];
		simulation.run();
            }
        });
        this.setResizable(false);
        labelJugadores.setBounds(20,20,170,20);
        spinnerJugadores.setBounds(170,20,200,20);
        buttonAccept.setBounds(120,60,130,60);
        this.add(labelJugadores);
        this.add(spinnerJugadores);
        this.add(buttonAccept);
        this.setLayout(null);
        this.pack();
        this.setVisible(true);
    }
}
