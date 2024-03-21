package org.example;

import org.example.model.Calculator;
import org.example.model.Polinom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InterfataGrafica extends JFrame implements ActionListener {
    private final JTextField polinom1Field, polinom2Field, rezultatField;
    private final JComboBox<String> operatieComboBox;
    public InterfataGrafica() {
        setTitle("Calculator polinomial");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel operatieLabel = new JLabel("Operatie:");
        operatieComboBox = new JComboBox<>(new String[]{"Adunare", "Scadere", "Inmultire", "Impartire", "Integrare", "Derivare"});
        JLabel polinom1Label = new JLabel("Polinom 1:");
        polinom1Field = new JTextField(20);
        JLabel polinom2Label = new JLabel("Polinom 2:");
        polinom2Field = new JTextField(20);
        JLabel rezultatLabel = new JLabel("Rezultat:");
        rezultatField = new JTextField(20);
        rezultatField.setEditable(false);
        JPanel intrarePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel intrarePanel2 = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel intrarePanel3 = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel rezultatPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        intrarePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        intrarePanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        intrarePanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rezultatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        intrarePanel.setPreferredSize(new Dimension(600, 60));
        intrarePanel2.setPreferredSize(new Dimension(600, 60));
        intrarePanel3.setPreferredSize(new Dimension(600, 60));
        rezultatPanel.setPreferredSize(new Dimension(600, 60));
        intrarePanel.setLayout(new BoxLayout(intrarePanel, BoxLayout.X_AXIS));
        intrarePanel2.setLayout(new BoxLayout(intrarePanel2, BoxLayout.X_AXIS));
        intrarePanel3.setLayout(new BoxLayout(intrarePanel3, BoxLayout.X_AXIS));
        rezultatPanel.setLayout(new BoxLayout(rezultatPanel, BoxLayout.X_AXIS));
        JPanel butonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton calculButton = new JButton("Calculeaza");
        butonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        butonPanel.setPreferredSize(new Dimension(600, 60));

        intrarePanel.add(polinom1Label);
        intrarePanel.add(polinom1Field);
        intrarePanel2.add(polinom2Label);
        intrarePanel2.add(polinom2Field);
        intrarePanel3.add(operatieLabel);
        intrarePanel3.add(operatieComboBox);
        rezultatPanel.add(rezultatLabel);
        rezultatPanel.add(rezultatField);
        calculButton.addActionListener(this);
        butonPanel.add(calculButton);
        Container contentPane = getContentPane();

        JPanel dateDeIntrarePanel = new JPanel(new BorderLayout());
        dateDeIntrarePanel.add(intrarePanel, BorderLayout.NORTH);
        dateDeIntrarePanel.add(intrarePanel2, BorderLayout.CENTER);
        dateDeIntrarePanel.add(intrarePanel3, BorderLayout.SOUTH);
        intrarePanel.setBackground(Color.CYAN);
        intrarePanel2.setBackground(Color.CYAN);
        intrarePanel3.setBackground(Color.WHITE);
        dateDeIntrarePanel.setBackground(Color.WHITE);
        butonPanel.setBackground(Color.LIGHT_GRAY);
        rezultatPanel.setBackground(Color.LIGHT_GRAY);
        contentPane.add(dateDeIntrarePanel, BorderLayout.NORTH);
        contentPane.add(butonPanel, BorderLayout.CENTER);
        contentPane.add(rezultatPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Calculeaza")) {
            Calculator c = new Calculator();
            Polinom p1 = convert(polinom1Field.getText());
            Polinom p2 = convert(polinom2Field.getText());
            Polinom rez;
            String operatie = (String) operatieComboBox.getSelectedItem();

            switch (operatie) {
                case "Adunare":
                    rez=c.adunare(p1, p2);
                    rezultatField.setText(rez.toString());
                    break;
                case "Scadere":
                    rez=c.scadere(p1, p2);
                    rezultatField.setText(rez.toString());
                    break;
                case "Inmultire":
                    rez=c.inmultire(p1, p2);
                    rezultatField.setText(rez.toString());
                    break;
                case "Impartire":
                    if(p2.toString() == "0")
                        JOptionPane.showMessageDialog(null, "Impartitorul nu poate fi 0");
                    else {
                        rez=c.impartire(p1, p2);
                        rezultatField.setText(rez.toString());
                    }
                    break;
                case "Integrare":
                    rez=c.integrare(p1);
                    String r = rez.toString();
                    if(p2.toString() != "0") {
                        r += " || ";
                        rez = c.integrare(p2);
                        r += rez;
                    }
                    rezultatField.setText(r);
                    break;
                case "Derivare":
                    rez=c.derivare(p1);
                    String r2 = rez.toString();
                    if(p2.toString() != "0") {
                        r2 += " || ";
                        rez = c.derivare(p2);
                        r2 += rez;
                    }
                    rezultatField.setText(r2);
                    break;
            }
        }
    }

    public Polinom convert (String s) {
        Polinom p = new Polinom();
        Pattern pattern = Pattern.compile("([+-]?[^-+]+)");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String monom = matcher.group(1);
            if (monom.charAt(0) == '+') {
                monom = monom.substring(1);
            }
            String c = "1", e = "0";
            Pattern pattern2 = Pattern.compile("^(-?\\d*)?([a-z])?(\\^(\\d*))?$");
            Matcher matcher2 = pattern2.matcher(monom);

            if (matcher2.find()) {
                if (matcher2.group(1) != null && !matcher2.group(1).equals(""))
                    c = matcher2.group(1);
                if (matcher2.group(4) != null && !matcher2.group(4).equals(""))
                    e = matcher2.group(4);
                else if (matcher2.group(2) != null && !matcher2.group(2).equals(""))
                    e = "1";
            }
            int c2;
            if (c.equals("-")) {
                c2 = -1;
            } else {
                c2 = Integer.parseInt(c);
            }

            int e2;
            if (e.equals("")) {
                e2 = 1;
            } else {
                e2 = Integer.parseInt(e);
            }
            p.adaugaMonom(c2, e2);
        }
        return p;
    }
    public static void main(String[] args) {

        new InterfataGrafica();
    }
}
