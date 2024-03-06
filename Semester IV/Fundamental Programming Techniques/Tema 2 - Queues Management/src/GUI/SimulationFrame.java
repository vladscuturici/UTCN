package GUI;

import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;
import java.awt.event.ActionEvent;

public class SimulationFrame extends JFrame {
    private JTextField tMaxServiceTextField;
    private JTextField tMinServiceTextField;
    private JComboBox<SelectionPolicy> selectionPolicyComboBox;
    private JTextArea logTextArea;
    private JTextField numberOfServersTextField;
    private JTextField numberOfClientsTextField;
    private JTextField tMaxSimTextField;
    private JTextField tMaxArrivalTextField;
    private JTextField tMinArrivalTextField;
    private JTextField avgWaitingTimeTextField;
    private JScrollPane logScrollPane;
    private JPanel mainPanel;
    private JButton startSimulationButton;
    private JTextField avgServiceTimeTextField;
    private JTextField peakHourTextField;
    private JButton test1Button;
    private JButton test2Button;
    private JButton test3Button;

    public SimulationFrame() {
        initComponents();
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tema 2");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        selectionPolicyComboBox.setModel(new DefaultComboBoxModel<>(SelectionPolicy.values()));
        logTextArea.setBackground(Color.LIGHT_GRAY);
        logTextArea.setEditable(false);

        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int numberOfServers = Integer.parseInt(numberOfServersTextField.getText());
                    int numberOfClients = Integer.parseInt(numberOfClientsTextField.getText());
                    int tMaxSim = Integer.parseInt(tMaxSimTextField.getText());
                    int tMaxArrival = Integer.parseInt(tMaxArrivalTextField.getText());
                    int tMinArrival = Integer.parseInt(tMinArrivalTextField.getText());
                    int tMaxService = Integer.parseInt(tMaxServiceTextField.getText());
                    int tMinService = Integer.parseInt(tMinServiceTextField.getText());
                    SelectionPolicy selectionPolicy = (SelectionPolicy) selectionPolicyComboBox.getSelectedItem();

                    SimulationManager simulationManager = new SimulationManager(SimulationFrame.this);
                    simulationManager.startSimulation(numberOfClients, numberOfServers, tMaxSim, tMaxArrival, tMinArrival, tMaxService, tMinService, selectionPolicy);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SimulationFrame.this, "Invalid Input.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        test1Button.addActionListener(e -> loadTest(1));
        test2Button.addActionListener(e -> loadTest(2));
        test3Button.addActionListener(e -> loadTest(3));
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        startSimulationButton = new JButton("Start Simulation");
        startSimulationButton.setBackground(Color.green);
        numberOfServersTextField = new JTextField();
        numberOfClientsTextField = new JTextField();
        numberOfClientsTextField.setBackground(Color.LIGHT_GRAY);
        tMaxSimTextField = new JTextField();
        tMaxArrivalTextField = new JTextField();
        tMaxArrivalTextField.setBackground(Color.LIGHT_GRAY);
        tMinArrivalTextField = new JTextField();
        tMinArrivalTextField.setBackground(Color.LIGHT_GRAY);
        tMaxServiceTextField = new JTextField();
        tMinServiceTextField = new JTextField();
        selectionPolicyComboBox = new JComboBox<>();
        logTextArea = new JTextArea();
        logScrollPane = new JScrollPane(logTextArea);
        avgWaitingTimeTextField = new JTextField();avgServiceTimeTextField = new JTextField();
        peakHourTextField = new JTextField();
        test1Button = new JButton("Test 1");
        test1Button.setBackground(Color.CYAN);
        test2Button = new JButton("Test 2");
        test2Button.setBackground(Color.CYAN);
        test3Button = new JButton("Test 3");
        test3Button.setBackground(Color.CYAN);

        JPanel inputPanel = new JPanel(new GridLayout(12, 2));
        inputPanel.add(new JLabel("Number of Servers:"));
        inputPanel.add(numberOfServersTextField);
        inputPanel.add(new JLabel("Number of Clients:"));
        inputPanel.add(numberOfClientsTextField);
        inputPanel.add(new JLabel("tMaxSim:"));
        inputPanel.add(tMaxSimTextField);
        inputPanel.add(new JLabel("tMaxArrival:"));
        inputPanel.add(tMaxArrivalTextField);
        inputPanel.add(new JLabel("tMinArrival:"));
        inputPanel.add(tMinArrivalTextField);
        inputPanel.add(new JLabel("tMaxService:"));
        inputPanel.add(tMaxServiceTextField);
        inputPanel.add(new JLabel("tMinService:"));
        inputPanel.add(tMinServiceTextField);
        inputPanel.add(new JLabel("Selection Policy:"));
        inputPanel.add(selectionPolicyComboBox);
        inputPanel.add(test1Button);
        inputPanel.add(test2Button);
        inputPanel.add(test3Button);
        inputPanel.add(startSimulationButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(logScrollPane, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(3, 2));
        statsPanel.add(new JLabel("Average Waiting Time:"));
        statsPanel.add(avgWaitingTimeTextField);
        statsPanel.add(new JLabel("Average Service Time:"));
        statsPanel.add(avgServiceTimeTextField);
        statsPanel.add(new JLabel("Peak Hour:"));
        statsPanel.add(peakHourTextField);

        mainPanel.add(statsPanel, BorderLayout.SOUTH);
    }
    public void updateLogTextArea(String text) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(text);
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }
    public void updatePeakHourTextField(int x) {
        peakHourTextField.setText(String.valueOf(x));
    }
    public void updateAvgServiceTimeTextField(double x) {
        avgServiceTimeTextField.setText(String.valueOf(x));
    }
    public void updateAvgWaitingTimeTextField(double x) {
        avgWaitingTimeTextField.setText(String.valueOf(x));
    }
    private void loadTest(int testNumber) {
        switch (testNumber) {
            case 1:
                numberOfServersTextField.setText("2");
                numberOfClientsTextField.setText("4");
                tMaxSimTextField.setText("60");
                tMaxArrivalTextField.setText("30");
                tMinArrivalTextField.setText("2");
                tMaxServiceTextField.setText("4");
                tMinServiceTextField.setText("2");
                selectionPolicyComboBox.setSelectedItem(SelectionPolicy.SHORTEST_TIME);
                break;
            case 2:
                numberOfServersTextField.setText("5");
                numberOfClientsTextField.setText("50");
                tMaxSimTextField.setText("60");
                tMaxArrivalTextField.setText("40");
                tMinArrivalTextField.setText("2");
                tMaxServiceTextField.setText("7");
                tMinServiceTextField.setText("1");
                selectionPolicyComboBox.setSelectedItem(SelectionPolicy.SHORTEST_TIME);
                break;
            case 3:
                numberOfServersTextField.setText("20");
                numberOfClientsTextField.setText("1000");
                tMaxSimTextField.setText("200");
                tMaxArrivalTextField.setText("100");
                tMinArrivalTextField.setText("10");
                tMaxServiceTextField.setText("9");
                tMinServiceTextField.setText("3");
                selectionPolicyComboBox.setSelectedItem(SelectionPolicy.SHORTEST_TIME);
                break;
        }
    }
}