package Presentation;

import Connection.ConnectionFactory;
import DataAccess.OrderDAO;
import DataAccess.ProductDAO;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import Model.Order;
import Model.Product;

public class GUI {
    private JFrame frame;
    private JTabbedPane tabbedPane;

    public GUI() {
        initialize();
        populateTables();
    }

    private void initialize() {
        frame = new JFrame("Tema 3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 800, 600);

        tabbedPane = new JTabbedPane();

        frame.getContentPane().add(tabbedPane);
        frame.getContentPane().setBackground(Color.CYAN);

        frame.setVisible(true);
    }

    private void populateTables() {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SHOW TABLES");

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);

                List<List<Object>> tableData = new ArrayList<>();
                List<Object> columnNames = new ArrayList<>();

                Statement dataStatement = connection.createStatement();
                ResultSet dataResultSet = dataStatement.executeQuery("SELECT * FROM " + tableName);

                ResultSetMetaData metaData = dataResultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }

                while (dataResultSet.next()) {
                    List<Object> rowData = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.add(dataResultSet.getObject(i));
                    }
                    tableData.add(rowData);
                }

                TablePanel tablePanel = new TablePanel(tableName, tableData, columnNames);
                tabbedPane.setBackground(Color.getColor("00BDFF"));
                tabbedPane.addTab(tableName, tablePanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    private class TablePanel extends JPanel {
        private JTable table;
        private JButton deleteButton;
        private DefaultTableModel tableModel;
        private JButton addButton;
        private JButton refreshButton;
        private JButton editButton;
        public TablePanel(String tableName, List<List<Object>> tableData, List<Object> columnNames) {
            setLayout(new BorderLayout());

            tableModel = new DefaultTableModel();
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setBackground(Color.getColor("BDA1EE"));
            scrollPane.setBackground(Color.getColor("B68CFF"));
            add(scrollPane, BorderLayout.CENTER);

            tableModel.setDataVector(convertListTo2DArray(tableData), columnNames.toArray());


            addButton = new JButton("Add");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            refreshButton = new JButton("Refresh Product");

            JButton addOrderButton = new JButton("Add Order");
            addOrderButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openOrderDialog();
                }
            });

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addRow();
                }
            });

            editButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editRow();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteRow();
                }
            });


            refreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTableProduct();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(addOrderButton);
            buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private Object[][] convertListTo2DArray(List<List<Object>> data) {
            Object[][] dataArray = new Object[data.size()][];
            for (int i = 0; i < data.size(); i++) {
                List<Object> rowList = data.get(i);
                dataArray[i] = rowList.toArray(new Object[0]);
            }
            return dataArray;
        }

        private void addRow() {
            String id = JOptionPane.showInputDialog(frame, "id:");

            String name = JOptionPane.showInputDialog(frame, "name:");

            String email = JOptionPane.showInputDialog(frame, "email / quantity:");

            tableModel.addRow(new Object[]{id, name, email});

            int lastRowIndex = tableModel.getRowCount() - 1;
            List<Object> rowData = new ArrayList<>();
            rowData.add(id);
            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                rowData.add(tableModel.getValueAt(lastRowIndex, i));
            }

            String tableName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());

            try {
                Connection connection = ConnectionFactory.getConnection();
                Statement statement = connection.createStatement();

                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");
                for (Object value : rowData) {
                    if (value instanceof String) {
                        queryBuilder.append("'").append(value).append("'");
                    } else {
                        queryBuilder.append(value);
                    }
                    queryBuilder.append(", ");
                }
                queryBuilder.setLength(queryBuilder.length() - 2);
                queryBuilder.append(")");

                statement.executeUpdate(queryBuilder.toString());

                ConnectionFactory.close(statement);
                ConnectionFactory.close(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void editRow() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String tableName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());

                String primaryKeyColumnName = tableModel.getColumnName(0);
                Object primaryKeyValue = tableModel.getValueAt(selectedRow, 0);

                List<Object> newValues = new ArrayList<>();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    Object value = JOptionPane.showInputDialog(frame, table.getColumnName(i), tableModel.getValueAt(selectedRow, i));
                    newValues.add(value);
                }

                try {
                    Connection connection = ConnectionFactory.getConnection();
                    Statement statement = connection.createStatement();

                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
                    for (int i = 1; i < tableModel.getColumnCount(); i++) {
                        queryBuilder.append(tableModel.getColumnName(i)).append(" = ");
                        if (newValues.get(i) instanceof String) {
                            queryBuilder.append("'").append(newValues.get(i)).append("'");
                        } else {
                            queryBuilder.append(newValues.get(i));
                        }
                        queryBuilder.append(", ");
                    }
                    queryBuilder.setLength(queryBuilder.length() - 2);
                    queryBuilder.append(" WHERE ").append(primaryKeyColumnName).append(" = ");
                    if (primaryKeyValue instanceof String) {
                        queryBuilder.append("'").append(primaryKeyValue).append("'");
                    } else {
                        queryBuilder.append(primaryKeyValue);
                    }
                    statement.executeUpdate(queryBuilder.toString());

                    ConnectionFactory.close(statement);
                    ConnectionFactory.close(connection);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 1; i < tableModel.getColumnCount(); i++) {
                    tableModel.setValueAt(newValues.get(i), selectedRow, i);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No row selected", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteRow() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String tableName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());

                String primaryKeyColumnName = tableModel.getColumnName(0);
                Object primaryKeyValue = tableModel.getValueAt(selectedRow, 0);
                tableModel.removeRow(selectedRow);

                try {
                    Connection connection = ConnectionFactory.getConnection();
                    Statement statement = connection.createStatement();

                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ")
                            .append(primaryKeyColumnName).append(" = ");
                    if (primaryKeyValue instanceof String) {
                        queryBuilder.append("'").append(primaryKeyValue).append("'");
                    } else {
                        queryBuilder.append(primaryKeyValue);
                    }

                    statement.executeUpdate(queryBuilder.toString());

                    ConnectionFactory.close(statement);
                    ConnectionFactory.close(connection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No row selected", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void openOrderDialog() {
            JDialog dialog = new JDialog(frame, "Add Order", true);

            JTextField idField = new JTextField();
            JComboBox<String> clientComboBox = new JComboBox<>(getClientNames());
            JComboBox<String> productComboBox = new JComboBox<>(getProductNames());

            JTextField quantityField = new JTextField();

            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String clientName = (String) clientComboBox.getSelectedItem();
                    String productName = (String) productComboBox.getSelectedItem();
                    int quantity, id;
                    try {
                        quantity = Integer.parseInt(quantityField.getText());
                        id = Integer.parseInt(idField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid quantity or id", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    addOrder(id, clientName, productName, quantity);
                    refreshTableProduct();
                    refreshTableOrder();
                    dialog.dispose();
                }
            });
            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Id:"));
            panel.add(idField);
            panel.add(new JLabel("Client:"));
            panel.add(clientComboBox);
            panel.add(new JLabel("Product:"));
            panel.add(productComboBox);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(submitButton);

            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        }

        private String[] getClientNames() {
            Statement statement = null;
            ResultSet resultSet = null;
            Connection connection = null;

            try {
                connection = ConnectionFactory.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT name FROM Client");

                ArrayList<String> names = new ArrayList<>();
                while (resultSet.next()) {
                    names.add(resultSet.getString("name"));
                }

                return names.toArray(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConnectionFactory.close(resultSet);
                ConnectionFactory.close(statement);
                ConnectionFactory.close(connection);
            }
            return new String[0];
        }

        private String[] getProductNames() {
            ResultSet resultSet = null;
            Connection connection = null;
            Statement statement = null;

            try {
                connection = ConnectionFactory.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT name FROM Product");

                ArrayList<String> names = new ArrayList<>();
                while (resultSet.next()) {
                    names.add(resultSet.getString("name"));
                }

                return names.toArray(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConnectionFactory.close(connection);
                ConnectionFactory.close(resultSet);
                ConnectionFactory.close(statement);
            }
            return new String[0];
        }

        private int getIdByName(String tableName, String name) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                connection = ConnectionFactory.getConnection();
                String query = "SELECT id FROM " + tableName + " WHERE name = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, name);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new Exception("No record found with name: " + name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConnectionFactory.close(statement);
                ConnectionFactory.close(connection);
                ConnectionFactory.close(resultSet);
            }

            return -1;
        }

        private void addOrder(int id, String clientName, String productName, int quantity) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                connection = ConnectionFactory.getConnection();

                int clientId = getIdByName("Client", clientName);
                int productId = getIdByName("Product", productName);

                String query = "SELECT quantity FROM Product WHERE id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, productId);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int productQuantity = resultSet.getInt("quantity");

                    if (productQuantity < quantity) {
                        JOptionPane.showMessageDialog(frame, "Not enough product quantity", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    query = "UPDATE Product SET quantity = quantity - ? WHERE id = ?";
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, quantity);
                    statement.setInt(2, productId);
                    statement.executeUpdate();
                    query = "INSERT INTO Orders (id, client_id, product_id, quantity) VALUES (?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, id);
                    statement.setInt(2, clientId);
                    statement.setInt(3, productId);
                    statement.setInt(4, quantity);
                    statement.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConnectionFactory.close(resultSet);
                ConnectionFactory.close(statement);
                ConnectionFactory.close(connection);
            }
        }
        private void refreshTableOrder() {
            List<Order> orders = OrderDAO.findAll();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Order order : orders) {
                model.addRow(new Object[]{order.getId(), order.getClientId(), order.getProductId(), order.getQuantity()});
            }
        }

        private void refreshTableProduct() {
            List<Product> products = ProductDAO.findAll();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Product product : products) {
                model.addRow(new Object[]{product.getId(), product.getName(), product.getQuantity()});
            }
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}