package DataAccess;

import Model.Order;
import Connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {
    protected static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    private static final String INSERT_QUERY = "INSERT INTO Orders (id, client_id, product_id) VALUES (?,?,?)";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM Orders WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM Orders";
    private static final String DELETE_QUERY = "DELETE FROM Orders WHERE id = ?";

    public static void insert(Order order) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_QUERY);
            preparedStatement.setInt(1, order.getId());
            preparedStatement.setInt(2, order.getClientId());
            preparedStatement.setInt(3, order.getProductId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "OrderDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close(connection);
        }
    }

    public static Order findById(int id) {
        Order order = null;
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int clientId = resultSet.getInt("client_id");
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quanitity");
                order = new Order(id, clientId, productId, quantity);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "OrderDAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close(connection);
        }
        return order;
    }

    public static List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int clientId = resultSet.getInt("client_id");
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");
                Order order = new Order(id, clientId, productId, quantity);
                orders.add(order);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "OrderDAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close(connection);
        }
        return orders;
    }

    public static void delete(int id) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "OrderDAO:delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close(connection);
        }
    }
}